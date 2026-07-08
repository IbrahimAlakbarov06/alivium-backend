package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.request.CreatePaymentIntentRequest;
import alivium.model.dto.response.PaymentIntentResponse;
import alivium.model.dto.response.PaymentResponse;
import alivium.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-intent")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreatePaymentIntentRequest request) {
        return ResponseEntity.ok(paymentService.createPaymentIntent(user.getId(), request));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String signatureHeader) {
        paymentService.handleWebhookEvent(payload, signatureHeader);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId, user.getId()));
    }
}
