package alivium.service.impl;

import alivium.domain.entity.Feedback;
import alivium.domain.entity.User;
import alivium.domain.repository.FeedbackRepository;
import alivium.domain.repository.UserRepository;
import alivium.exception.NotFoundException;
import alivium.mapper.FeedbackMapper;
import alivium.model.dto.request.FeedbackRequest;
import alivium.model.dto.response.FeedbackAdminResponse;
import alivium.model.dto.response.FeedbackResponse;
import alivium.model.enums.FeedbackStatus;
import alivium.model.enums.FeedbackType;
import alivium.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @CacheEvict(value = "feedbacks", allEntries = true)
    public FeedbackResponse createFeedback(Long userId, FeedbackRequest request) {
        User user = findUserById(userId);
        Feedback feedback = feedbackMapper.toEntity(request, user);
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return feedbackMapper.toResponse(savedFeedback);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "feedbacks", key = "'my_' + #userId")
    public List<FeedbackResponse> getMyFeedbacks(Long userId) {
        List<Feedback> myFeedbacks = feedbackRepository.findByUserId(userId);
        return myFeedbacks.stream()
                .map(feedbackMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeedbackAdminResponse> getAllFeedbacks(Pageable pageable) {
        return feedbackRepository.findAll(pageable)
                .map(feedbackMapper::toAdminResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "feedbacks", key = "'id_' + #id")
    public FeedbackAdminResponse getFeedbackById(Long id) {
        Feedback feedback = findFeedbackById(id);
        return feedbackMapper.toAdminResponse(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "feedbacks", key = "'status_' + #status")
    public List<FeedbackAdminResponse> findFeedbackByStatus(FeedbackStatus status) {
        List<Feedback> feedbacks = feedbackRepository.findByStatus(status);
        return feedbacks.stream()
                .map(feedbackMapper::toAdminResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "feedbacks", key = "'type_' + #type")
    public List<FeedbackAdminResponse> findFeedbackByType(FeedbackType type) {
        List<Feedback> feedbacks = feedbackRepository.findByType(type);
        return feedbacks.stream()
                .map(feedbackMapper::toAdminResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "feedbacks", allEntries = true)
    public void updateFeedbackStatus(Long id, FeedbackStatus status) {
        Feedback feedback = findFeedbackById(id);
        feedback.setStatus(status);
        feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    @CacheEvict(value = "feedbacks", allEntries = true)
    public void deleteFeedback(Long id) {
        Feedback feedback = findFeedbackById(id);
        feedbackRepository.delete(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "feedbacks", key = "'totalCount'")
    public Long getTotalFeedbackCount() {
        return feedbackRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "feedbacks", key = "'averageRating'")
    public Double getAverageRating() {
        Double avg = feedbackRepository.calculateAverageRating();
        return avg != null ? avg : 0.0;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Feedback findFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Feedback not found with id :" + id));
    }
}
