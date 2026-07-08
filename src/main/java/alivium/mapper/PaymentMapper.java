package alivium.mapper;

import alivium.domain.entity.Payment;
import alivium.model.dto.response.PaymentIntentResponse;
import alivium.model.dto.response.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .orderNumber(payment.getOrder() != null ? payment.getOrder().getOrderNumber() : null)
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .stripePaymentIntentId(payment.getStripePaymentIntentId())
                .failureReason(payment.getFailureReason())
                .build();
    }

    public PaymentIntentResponse toIntentResponse(Payment payment, String clientSecret,
                                                  String publishableKey, String currency) {
        return PaymentIntentResponse.builder()
                .orderId(payment.getOrder().getId())
                .orderNumber(payment.getOrder().getOrderNumber())
                .paymentIntentId(payment.getStripePaymentIntentId())
                .clientSecret(clientSecret)
                .publishableKey(publishableKey)
                .amount(payment.getAmount())
                .currency(currency)
                .build();
    }
}
