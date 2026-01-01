package alivium.model.dto.response;

import alivium.model.enums.FeedbackStatus;
import alivium.model.enums.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackAdminResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private FeedbackType type;
    private String comment;
    private Integer rating;
    private FeedbackStatus status;
    private LocalDateTime createdAt;
}
