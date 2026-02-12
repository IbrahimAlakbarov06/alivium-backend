package alivium.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCardResponse {
    private Long id;
    private String last4Digits;
    private String cardType;
    private boolean defaultCard;
    private Integer expiryMonth;
    private Integer expiryYear;
}
