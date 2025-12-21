package alivium.model.dto.response;

import alivium.model.enums.ChatStatus;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long adminId;
    private String adminFullName;
    private ChatStatus status;
    private LocalDateTime createdAt;
}
