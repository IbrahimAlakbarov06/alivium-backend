package alivium.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayriffCreateOrderResponse {
    private String code;
    private String message;
    private String route;
    private String internalMessage;
    private String responseId;
    private Payload payload;

    @Data
    public static class Payload {
        private String orderId;
        private String paymentUrl;
        private Long transactionId;
    }
}
