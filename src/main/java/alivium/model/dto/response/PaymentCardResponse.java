package alivium.model.dto.response;

import alivium.model.enums.CardType;
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
    private String maskedCardNumber;
    private CardType cardType;
    private boolean isDefault;
    private Integer expiryMonth;
    private Integer expiryYear;
}
