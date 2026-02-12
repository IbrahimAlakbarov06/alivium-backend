package alivium.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddPaymentCardRequest {

    private String cardNumber;
    private Integer expiryMonth;
    private Integer expiryYear;
    private String cvv;
    private boolean makeDefault;
}
