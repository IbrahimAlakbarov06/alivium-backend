package alivium.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMessageWsRequest {
    private Long messageId;
    private Long chatRoomId;
    private Long userId;
}
