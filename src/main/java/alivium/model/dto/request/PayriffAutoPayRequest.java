package alivium.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayriffAutoPayRequest {
    private String cardUuid;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String callbackUrl;
    private String operation;
}
