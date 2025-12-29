package alivium.model.dto.response;
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
public class FeedbackResponse {
    private Long id;
    private FeedbackType type;
    private String comment;
    private Integer rating;
    private LocalDateTime createdAt;
}
