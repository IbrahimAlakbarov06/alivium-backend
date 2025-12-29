package alivium.service;

import alivium.model.dto.request.FeedbackRequest;
import alivium.model.dto.response.FeedbackAdminResponse;
import alivium.model.dto.response.FeedbackResponse;
import alivium.model.enums.FeedbackStatus;
import alivium.model.enums.FeedbackType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedbackService {
    FeedbackResponse createFeedback(Long userId,FeedbackRequest request);

    List<FeedbackResponse> getMyFeedbacks(Long userId);

    Page<FeedbackAdminResponse> getAllFeedbacks(Pageable pageable);

    FeedbackAdminResponse getFeedbackById(Long id);

    List<FeedbackResponse> findFeedbackByStatus(FeedbackStatus status);

    List<FeedbackResponse> findFeedbackByType(FeedbackType type);

    void updateFeedbackStatus(Long id, FeedbackStatus status);

    void deleteFeedback(Long id);

    Long getTotalFeedbackCount();

    Double getAverageRating();
}
