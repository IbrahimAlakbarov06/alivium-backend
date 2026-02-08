package alivium.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayriffCreateOrderRequest {
    private BigDecimal amount;
    private String currency;
    private String language;
    private String description;
    private String callbackUrl;
    private Boolean cardSave;
    private String operation;
    private Map<String, Object> metadata;
}
