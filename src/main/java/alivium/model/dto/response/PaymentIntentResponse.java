package alivium.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentResponse {
    private Long orderId;
    private String orderNumber;
    private String paymentIntentId;
    private String clientSecret;
    private String publishableKey;
    private BigDecimal amount;
    private String currency;
}
