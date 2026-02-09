package alivium.mapper;

import alivium.domain.entity.*;
import alivium.model.dto.request.CreateOrderRequest;
import alivium.model.dto.response.OrderItemResponse;
import alivium.model.dto.response.OrderResponse;
import alivium.model.dto.response.OrderSummaryResponse;
import alivium.model.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order toEntity(User user, Address address, CreateOrderRequest request){
        return Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .billingAddress(address)
                .status(OrderStatus.PENDING)
                .notes(request.getNotes())
                .discountAmount(BigDecimal.ZERO)
                .build();
    }

    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponse> items= order.getOrderItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());

        int totalItems = order.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        String deliveryAddress= formatAddress(order.getBillingAddress());

        String estimatedDelivery =null;
        if (order.getShipping()!=null && order.getShipping().getEstimatedDeliveryDate()!=null) {
            estimatedDelivery = order.getShipping().getEstimatedDeliveryDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())

                .items(items)
                .totalItems(totalItems)

                .subtotal(order.getSubtotal())
                .discount(order.getDiscountAmount())
                .shipping(order.getShipping() != null ? order.getShipping().getCost() : null)
                .totalPrice(order.getTotalAmount())

                .trackingNumber(order.getShipping() != null ? order.getShipping().getTrackingNumber() : null)
                .shippingMethod(order.getShipping() != null ? order.getShipping().getShippingMethod() : null)
                .estimatedDelivery(estimatedDelivery)

                .paymentMethod(order.getPayment() != null ? order.getPayment().getPaymentMethod() : null)
                .paymentStatus(order.getPayment() != null ? order.getPayment().getStatus() : null)

                .deliveryAddress(deliveryAddress)

                .voucherCode(order.getVoucher() != null ? order.getVoucher().getCode() : null)

                .notes(order.getNotes())

                .cancellationReason(order.getCancellationReason())
                .cancellationDate(order.getCancelledAt())

                .build();
    }

    public OrderItemResponse toOrderItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }

        String productImage = null;
        if (item.getProduct() != null && item.getProduct().getImages() != null) {
            productImage = item.getProduct().getImages().stream()
                    .filter(ProductImage::getIsPrimary)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElseGet(() ->
                            item.getProduct().getImages().stream()
                                    .map(ProductImage::getImageUrl)
                                    .findFirst()
                                    .orElse(null)
                    );
        }

        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct().getName())
                .productImage(productImage)
                .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                .variantColor(item.getVariant().getColor())
                .variantSize(item.getVariant().getSize())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    public OrderSummaryResponse toSummaryResponse(Order order) {
        if (order == null) {
            return null;
        }

        int totalItems = order.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        String trackingNumber= order.getShipping() != null
                ? order.getShipping().getTrackingNumber()
                : null;

        return OrderSummaryResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .trackingNumber(trackingNumber)
                .totalItems(totalItems)
                .subtotal(order.getSubtotal())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();

    }

    public List<OrderResponse> toListResponse(List<Order> orders) {
        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrderSummaryResponse> toSummaryResponseList(List<Order> orders) {
        return orders.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }


    private String formatAddress(Address address) {
        if (address == null) {
            return null;
        }

        StringBuilder sb= new StringBuilder();

        if (address.getFullName() != null) {
            sb.append(address.getFullName()).append(", ");
        }

        if (address.getStreet() != null) {
            sb.append(address.getStreet()).append(", ");
        }

        if (address.getCity() != null) {
            sb.append(address.getCity()).append(", ");
        }

        if (address.getCountry() != null) {
            sb.append(address.getCountry()).append(", ");
        }

        return sb.toString();
    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }

    private String generateTrackingNumber() {
        return "IK" + System.currentTimeMillis();
    }
}
