package alivium.model.dto.request;

import alivium.model.enums.FeedbackType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackRequest {
    @NotNull(message = "Feedback type cannot be null")
    private FeedbackType type;

    @NotBlank(message = "Comment cannot be blank")
    @Size(max = 200, message = "Comment cannot exceed 500 characters")
    private String comment;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;
}
