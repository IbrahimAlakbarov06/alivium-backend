package alivium.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditMessageWsRequest {
    private Long messageId;
    private Long chatRoomId;
    private Long userId;
    private String newMessage;
}
