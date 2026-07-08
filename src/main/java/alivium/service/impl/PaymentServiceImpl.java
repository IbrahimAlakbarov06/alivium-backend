package alivium.service.impl;

import alivium.config.StripeProperties;
import alivium.domain.entity.Order;
import alivium.domain.entity.Payment;
import alivium.domain.repository.OrderRepository;
import alivium.domain.repository.PaymentRepository;
import alivium.exception.BusinessException;
import alivium.exception.NotFoundException;
import alivium.mapper.PaymentMapper;
import alivium.model.dto.request.CreatePaymentIntentRequest;
import alivium.model.dto.response.PaymentIntentResponse;
import alivium.model.dto.response.PaymentResponse;
import alivium.model.enums.NotificationTemplate;
import alivium.model.enums.OrderStatus;
import alivium.model.enums.PaymentMethod;
import alivium.model.enums.PaymentStatus;
import alivium.service.NotificationTemplateService;
import alivium.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeProperties stripeProperties;
    private final NotificationTemplateService notificationService;

    @Override
    @Transactional
    public PaymentIntentResponse createPaymentIntent(Long userId, CreatePaymentIntentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found: " + request.getOrderId()));

        checkOwnership(order, userId);

        Payment payment = order.getPayment();
        validatePayableByCard(order, payment);

        try {
            PaymentIntent intent = resolvePaymentIntent(order, payment);

            payment.setStripePaymentIntentId(intent.getId());
            paymentRepository.save(payment);

            return paymentMapper.toIntentResponse(payment, intent.getClientSecret(),
                    stripeProperties.getPublishableKey(), stripeProperties.getCurrency());
        } catch (StripeException e) {
            throw new BusinessException("Payment provider error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"orders", "userOrders", "userOrdersByStatus", "allOrders", "ordersByStatus"}, allEntries = true)
    public void handleWebhookEvent(String payload, String signatureHeader) {
        if (signatureHeader == null || signatureHeader.isBlank()) {
            throw new BusinessException("Missing Stripe signature header");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, stripeProperties.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            throw new BusinessException("Invalid webhook signature");
        } catch (RuntimeException e) {
            // constructEvent parses the payload with Gson before verifying the
            // signature, so malformed JSON surfaces as an unchecked exception
            throw new BusinessException("Invalid webhook payload");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(extractPaymentIntent(event));
                break;

            case "payment_intent.payment_failed":
                handlePaymentIntentFailed(extractPaymentIntent(event));
                break;

            default:
                log.debug("Ignoring unhandled Stripe event type: {}", event.getType());
                break;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId, Long userId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Payment not found for order: " + orderId));

        checkOwnership(payment.getOrder(), userId);

        return paymentMapper.toResponse(payment);
    }

    private void validatePayableByCard(Order order, Payment payment) {
        if (payment == null) {
            throw new BusinessException("Order has no payment record");
        }

        if (payment.getPaymentMethod() != PaymentMethod.CARD) {
            throw new BusinessException("Order payment method is " + payment.getPaymentMethod()
                    + ". Only CARD orders can be paid online");
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new BusinessException("Order is already paid");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Order cannot be paid. Status: " + order.getStatus());
        }

        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Order total amount must be positive");
        }
    }

    private PaymentIntent resolvePaymentIntent(Order order, Payment payment) throws StripeException {
        if (payment.getStripePaymentIntentId() != null) {
            PaymentIntent existing = PaymentIntent.retrieve(payment.getStripePaymentIntentId());

            if ("succeeded".equals(existing.getStatus())) {
                throw new BusinessException("Payment has already succeeded for this order");
            }

            if (!"canceled".equals(existing.getStatus())) {
                return existing;
            }
        }

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(toMinorUnits(order.getTotalAmount()))
                .setCurrency(stripeProperties.getCurrency())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("orderId", String.valueOf(order.getId()))
                .putMetadata("orderNumber", order.getOrderNumber())
                .build();

        return PaymentIntent.create(params);
    }

    private void handlePaymentIntentSucceeded(PaymentIntent intent) {
        Payment payment = findPaymentForIntent(intent);
        if (payment == null) {
            return;
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setFailureReason(null);
        payment.setStripePaymentIntentId(intent.getId());

        Order order = payment.getOrder();
        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CONFIRMED);
            sendOrderConfirmedNotification(order);
        }

        paymentRepository.save(payment);
        log.info("Payment succeeded for order {}, intent {}", order.getOrderNumber(), intent.getId());
    }

    private void handlePaymentIntentFailed(PaymentIntent intent) {
        Payment payment = findPaymentForIntent(intent);
        if (payment == null) {
            return;
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(extractFailureReason(intent));

        paymentRepository.save(payment);
        log.warn("Payment failed for order {}, intent {}: {}",
                payment.getOrder().getOrderNumber(), intent.getId(), payment.getFailureReason());
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);

        if (!(stripeObject instanceof PaymentIntent)) {
            throw new BusinessException("Unable to deserialize Stripe event data for event: " + event.getId());
        }

        return (PaymentIntent) stripeObject;
    }

    private Payment findPaymentForIntent(PaymentIntent intent) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(intent.getId()).orElse(null);

        if (payment == null && intent.getMetadata() != null && intent.getMetadata().get("orderId") != null) {
            Long orderId = Long.valueOf(intent.getMetadata().get("orderId"));
            payment = paymentRepository.findByOrderId(orderId).orElse(null);
        }

        if (payment == null) {
            log.warn("Received Stripe event for unknown payment intent: {}", intent.getId());
        }

        return payment;
    }

    private String extractFailureReason(PaymentIntent intent) {
        if (intent.getLastPaymentError() != null && intent.getLastPaymentError().getMessage() != null) {
            return intent.getLastPaymentError().getMessage();
        }
        return "Payment was declined";
    }

    private long toMinorUnits(BigDecimal amount) {
        return amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private void checkOwnership(Order order, Long userId) {
        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("Access denied");
        }
    }

    private void sendOrderConfirmedNotification(Order order) {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", order.getOrderNumber());
        notificationService.sendNotification(order.getUser(), NotificationTemplate.ORDER_CONFIRMED, map);
    }
}
