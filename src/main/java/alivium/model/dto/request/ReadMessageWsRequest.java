package alivium.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadMessageWsRequest {
    private Long messageId;
    private Long userId;
    private String username;
}
