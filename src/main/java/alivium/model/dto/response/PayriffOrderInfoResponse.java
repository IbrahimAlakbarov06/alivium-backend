package alivium.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayriffOrderInfoResponse {
    private String code;
    private String message;
    private Payload payload;

    @Data
    public static class Payload {
        private String orderId;
        private String paymentStatus;
        private BigDecimal amount;
        private String currencyType;

        private List<Transaction> transactions;
    }

    @Data
    public static class Transaction {
        private String uuid;
        private String status;
        private String requestRrn;
        private String responseRrn;
        private CardDetails cardDetails;
    }

    @Data
    public static class CardDetails {
        private String maskedPan;
        private String brand;
        private String cardHolderName;
    }
}
