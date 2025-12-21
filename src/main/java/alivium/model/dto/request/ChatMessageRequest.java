package alivium.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageRequest {

    @NotNull(message = "ChatRoom ID must not be null")
    private Long chatRoomId;

    @NotNull(message = "Sender ID must not be null")
    private Long senderId;

    @NotBlank(message = "Message must not be blank")
    private String message;
}
