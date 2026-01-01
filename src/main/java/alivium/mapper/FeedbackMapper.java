package alivium.mapper;

import alivium.domain.entity.Feedback;
import alivium.domain.entity.User;
import alivium.model.dto.request.FeedbackRequest;
import alivium.model.dto.response.FeedbackAdminResponse;
import alivium.model.dto.response.FeedbackResponse;
import alivium.model.enums.FeedbackStatus;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {
    public Feedback toEntity(FeedbackRequest request, User user) {
        if(request == null) return null;

        return Feedback.builder()
                .type(request.getType())
                .comment(request.getComment())
                .rating(request.getRating())
                .status(FeedbackStatus.NEW)
                .user(user)
                .build();
    }

    public FeedbackAdminResponse toAdminResponse(Feedback feedback) {
        if(feedback == null) return null;

        return FeedbackAdminResponse.builder()
                .id(feedback.getId())
                .type(feedback.getType())
                .comment(feedback.getComment())
                .rating(feedback.getRating())
                .createdAt(feedback.getCreatedAt())
                .userId(feedback.getUser().getId())
                .userEmail(feedback.getUser().getEmail())
                .userFullName(feedback.getUser().getFullName())
                .status(feedback.getStatus())
                .build();
    }

    public FeedbackResponse toResponse(Feedback feedback) {
        if(feedback == null) return null;

        return FeedbackResponse.builder()
                .id(feedback.getId())
                .type(feedback.getType())
                .comment(feedback.getComment())
                .rating(feedback.getRating())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
