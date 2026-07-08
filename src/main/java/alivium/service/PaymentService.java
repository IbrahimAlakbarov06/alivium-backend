package alivium.service;

import alivium.model.dto.request.CreatePaymentIntentRequest;
import alivium.model.dto.response.PaymentIntentResponse;
import alivium.model.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentIntentResponse createPaymentIntent(Long userId, CreatePaymentIntentRequest request);

    void handleWebhookEvent(String payload, String signatureHeader);

    PaymentResponse getPaymentByOrderId(Long orderId, Long userId);
}
