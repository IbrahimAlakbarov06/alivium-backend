package alivium.service.impl;

import alivium.domain.entity.*;
import alivium.domain.repository.*;
import alivium.exception.BusinessException;
import alivium.exception.NotFoundException;
import alivium.mapper.OrderMapper;
import alivium.model.dto.request.CancelOrderRequest;
import alivium.model.dto.request.CreateOrderRequest;
import alivium.model.dto.response.OrderResponse;
import alivium.model.dto.response.OrderSummaryResponse;
import alivium.model.enums.*;
import alivium.service.NotificationTemplateService;
import alivium.service.OrderService;
import alivium.service.VoucherUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final VoucherRepository voucherRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final VoucherUsageService voucherUsageService;
    private final NotificationTemplateService notificationService;

    @Override
    @Transactional
    @CacheEvict(value = {"orders", "userOrders", "userOrdersByStatus", "userOrderCount", "allOrders", "ordersByStatus", "carts"}, allEntries = true)
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = findUserById(userId);

        Cart cart = findCartByUserId(userId);
        validateCartNotEmpty(cart);

        Address address = resolveDelireveryAddress(userId, user, request);

        Order order = orderMapper.toEntity(user, address, request);

        addOrderItemsFromCart(order, cart);

        calculateSubtotal(order);

        applyVoucher(order, request, user);

        addShipping(order, request.getShippingMethod(), address);

        calculateTotal(order);

        addPayment(order, request);

        Order savedOrder = orderRepository.save(order);

        sendOrderPlacedNotification(user, savedOrder);

        reduceStock(cart);
        clearCart(cart);

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#orderId")
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userOrders", key = "#userId")
    public List<OrderSummaryResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orderMapper.toSummaryResponseList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userOrdersByStatus", key = "#userId + '-' + #status")
    public List<OrderSummaryResponse> getUserOrdersByStatus(Long userId, OrderStatus status) {
        List<Order> orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        return orderMapper.toSummaryResponseList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userOrderCount", key = "#userId")
    public Long getUserOrdersCount(Long userId) {
        return orderRepository.countByUserId(userId);
    }


    @Override
    @Transactional
    @CacheEvict(value = {"orders", "userOrders", "userOrdersByStatus", "userOrderCount", "allOrders", "ordersByStatus"}, allEntries = true)
    public OrderResponse cancelOrder(Long orderId, Long userId, CancelOrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        checkOwnership(order, userId);
        validateCancellable(order);

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancellationReason(request.getReason());

        restoreStock(order);

        if (order.getVoucher() != null) {
            voucherUsageService.cancelVoucherUsage(
                    userId,
                    order.getVoucher().getId(),
                    order.getId()
            );
        }

        Order cancelled = orderRepository.save(order);

        sendOrderCancelledNotification(cancelled);

        return orderMapper.toResponse(cancelled);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allOrders", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<OrderSummaryResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"orders", "userOrders", "userOrdersByStatus", "allOrders", "ordersByStatus"}, allEntries = true)
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        order.setStatus(status);

        updateShippingStatus(order, status);

        Order updated = orderRepository.save(order);

        sendOrderStatusNotification(updated, status);

        return orderMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "ordersByStatus", key = "#status")
    public List<OrderSummaryResponse> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(status);
        return orderMapper.toSummaryResponseList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getOrderByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByDateRange(startDate, endDate);
        return orderMapper.toSummaryResponseList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }


    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id " + userId));
    }

    private Cart findCartByUserId(Long userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found with user id " + userId));
    }

    private void validateCartNotEmpty(Cart cart) {
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new BusinessException("Cart is empty. Please add items before checkout");
        }
    }

    private Address resolveDelireveryAddress(Long userId, User user, CreateOrderRequest request) {
        if (request.getAddressId() != null) {
            return findAddressById(userId, request.getAddressId());
        }

        if (request.getAddress() != null) {
            return createNewAddress(user, request.getAddress());
        }

        throw new BusinessException("Delivery address is required");
    }

    private Address findAddressById(Long userId, Long addressId) {
        return addressRepository.findByIdAndUserId(userId, addressId)
                .orElseThrow(() -> new NotFoundException("Address not found with id " + addressId));
    }

    private Address createNewAddress(User user, CreateOrderRequest.AddressDetails details) {
        Address address = Address.builder()
                .user(user)
                .fullName(details.getFullName())
                .street(details.getStreet())
                .city(details.getCity())
                .country(details.getCountry())
                .zipCode(details.getZipCode())
                .phone(details.getPhoneNumber())
                .isDefault(false)
                .build();

        if (Boolean.TRUE.equals(details.getSaveForFuture())) {
            Address saved = addressRepository.save(address);
            return saved;
        }

        return address;
    }

    private void addOrderItemsFromCart(Order order, Cart cart) {
        for (CartItem cartItem : cart.getCartItems()) {
            validateStock(cartItem);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .variant(cartItem.getVariant())
                    .productName(cartItem.getProduct().getName())
                    .variantColor(cartItem.getVariant() != null ? cartItem.getVariant().getColor() : null)
                    .variantSize(cartItem.getVariant() != null ? cartItem.getVariant().getSize() : null)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getPrice())
                    .totalPrice(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build();

            order.getOrderItems().add(orderItem);
        }
    }

    private void validateStock(CartItem cartItem) {
        if (cartItem.getVariant() != null) {
            ProductVariant variant = cartItem.getVariant();

            if (!variant.getAvailable()) {
                throw new BusinessException(
                        String.format("Product '%s' (%s, %s) is not available",
                                cartItem.getProduct().getName(),
                                variant.getColor(),
                                variant.getSize())
                );
            }

            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new BusinessException(
                        String.format("Insufficient stock for '%s' (%s, %s). Available: %d, Requested: %d",
                                cartItem.getProduct().getName(),
                                variant.getColor(),
                                variant.getSize(),
                                variant.getStockQuantity(),
                                cartItem.getQuantity())
                );
            }
        } else if (!cartItem.getProduct().getActive()) {
            throw new BusinessException("Product '" + cartItem.getProduct().getName() + "' is not available");
        }
    }

    private void calculateSubtotal(Order order) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItem item : order.getOrderItems()) {
            subtotal = subtotal.add(item.getTotalPrice());
        }
        order.setSubtotal(subtotal);
    }

    private void applyVoucher(Order order, CreateOrderRequest request, User user) {
        if (request.getVoucherCode() == null || request.getVoucherCode().isBlank()) {
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setTotalAmount(order.getSubtotal());
            return;
        }

        Voucher voucher = voucherRepository.findByCode(request.getVoucherCode())
                .orElseThrow(() -> new NotFoundException("Voucher not found with code: " + request.getVoucherCode()));

        validateVoucher(voucher, order.getSubtotal(), user);

        BigDecimal discountAmount = calculateDiscount(voucher, order.getSubtotal());

        order.setVoucher(voucher);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(order.getSubtotal().subtract(discountAmount));

        voucherUsageService.recordVoucherUsage(user, voucher, order);
    }

    private void validateVoucher(Voucher voucher, BigDecimal orderTotal, User user) {
        if (!voucher.getIsActive()) {
            throw new BusinessException("Voucher is not active");
        }

        if (voucher.getExpiryDate() != null && voucher.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Voucher has expired");
        }

        if (voucher.getMinOrderAmount() != null && orderTotal.compareTo(voucher.getMinOrderAmount()) < 0) {
            throw new BusinessException(
                    String.format("Minimum order amount for this voucher is %s", voucher.getMinOrderAmount())
            );
        }

        if (!voucherUsageService.canUserUseVoucher(user, voucher)) {
            throw new BusinessException("You have exceeded the usage limit for this voucher");
        }
    }

    private BigDecimal calculateDiscount(Voucher voucher, BigDecimal subtotal) {
        BigDecimal discount;

        if (voucher.getType() == DiscountType.PERCENTAGE) {
            discount = subtotal.multiply(voucher.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = voucher.getDiscountValue();
        }

        if (voucher.getMaxDiscountAmount() != null && discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
            discount = voucher.getMaxDiscountAmount();
        }

        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        return discount;
    }

    private void addShipping(Order order, ShippingMethod method, Address address) {
        Shipping shipping = Shipping.builder()
                .order(order)
                .shippingMethod(method)
                .cost(method.getCost())
                .trackingNumber(generateTrackingNumber())
                .shippingAddress(orderMapper.formatAddress(address))
                .shippingStatus(ShippingStatus.PENDING)
                .estimatedDeliveryDate(LocalDateTime.now().plusDays(method.getEstimatedDays()))
                .deliveryAttempts(0)
                .build();

        order.setShipping(shipping);
        shipping.setOrder(order);
    }

    private void calculateTotal(Order order) {
        BigDecimal total = order.getSubtotal();

        if (order.getDiscountAmount() != null) {
            total = total.subtract(order.getDiscountAmount());
        }

        if (order.getShipping() != null && order.getShipping().getCost() != null) {
            total = total.add(order.getShipping().getCost());
        }

        order.setTotalAmount(total);
    }


    private void addPayment(Order order, CreateOrderRequest request) {
        Payment payment = Payment.builder()
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .build();

        order.setPayment(payment);

        // CASH orders are confirmed immediately; CARD orders stay PENDING
        // until the Stripe webhook reports payment_intent.succeeded
        if (request.getPaymentMethod() == PaymentMethod.CASH) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
    }

    private void reduceStock(Cart cart) {
        for (CartItem item : cart.getCartItems()) {
            if (item.getVariant() != null) {
                ProductVariant variant = item.getVariant();
                int newStock = variant.getStockQuantity() - item.getQuantity();
                variant.setStockQuantity(newStock);

                if (newStock <= 0) {
                    variant.setAvailable(false);
                }

                variantRepository.save(variant);
            }
        }
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            if (item.getVariant() != null) {
                ProductVariant variant = item.getVariant();
                int newStock = variant.getStockQuantity() + item.getQuantity();
                variant.setStockQuantity(newStock);
                variant.setAvailable(true);
                variantRepository.save(variant);
            }
        }
    }


    private void clearCart(Cart cart) {
        cartItemRepository.deleteByCartId(cart.getId());
        // the managed Cart still references the deleted items; without this the
        // CascadeType.ALL collection re-persists them on flush
        cart.getCartItems().clear();
    }


    private void updateShippingStatus(Order order, OrderStatus orderStatus) {
        if (order.getShipping() == null) return;

        if (orderStatus == OrderStatus.SHIPPED) {
            order.getShipping().setShippingStatus(ShippingStatus.IN_TRANSIT);
            order.getShipping().setShippedDate(LocalDateTime.now());
        } else if (orderStatus == OrderStatus.DELIVERED) {
            order.getShipping().setShippingStatus(ShippingStatus.DELIVERED);
            order.getShipping().setActualDeliveryDate(LocalDateTime.now());
        }
    }

    private void checkOwnership(Order order, Long userId) {
        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("Access denied");
        }
    }

    private void validateCancellable(Order order) {
        if (order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED ||
                order.getStatus() == OrderStatus.REFUNDED) {
            throw new BusinessException("Order cannot be cancelled. Status: " + order.getStatus());
        }
    }

    private String generateTrackingNumber() {
        return "IK" + System.currentTimeMillis();
    }

    private void sendOrderPlacedNotification(User user,Order order) {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", order.getOrderNumber());
        map.put("amount", order.getTotalAmount().toString());
        notificationService.sendNotification(user, NotificationTemplate.ORDER_PLACED,map);
    }

    private void sendOrderCancelledNotification(Order order) {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", order.getOrderNumber());
        map.put("reason", order.getCancellationReason());
        notificationService.sendNotification(order.getUser(), NotificationTemplate.ORDER_CANCELLED,map);
    }

    private void sendOrderStatusNotification(Order order, OrderStatus orderStatus) {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", order.getOrderNumber());

        switch (orderStatus){
            case CONFIRMED:
                notificationService.sendNotification(order.getUser(), NotificationTemplate.ORDER_CONFIRMED,map);
                break;

            case SHIPPED:
                map.put("trackingNumber", order.getShipping().getTrackingNumber());
                notificationService.sendNotification(order.getUser(), NotificationTemplate.ORDER_SHIPPED,map);
                break;

            case DELIVERED:
            notificationService.sendNotification(order.getUser(), NotificationTemplate.ORDER_DELIVERED,map);
            break;

            default:
                break;
        }
    }
}