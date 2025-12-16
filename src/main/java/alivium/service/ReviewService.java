package alivium.service;

import alivium.model.dto.request.ReviewRequest;
import alivium.model.dto.request.ReviewUpdateRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ProductRatingResponse;
import alivium.model.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(Long userId, ReviewRequest request);

    List<ReviewResponse> getUserReviews(Long userId);

    List<ReviewResponse> getProductReviews(Long productId);

    ProductRatingResponse getProductRatingStats(Long productId);

    ReviewResponse getReviewById(Long reviewId);

    ReviewResponse updateReview(Long userId, Long reviewId, ReviewUpdateRequest request);

    ReviewResponse toggleReviewStatus(Long reviewId);

    ReviewResponse verifyReview(Long reviewId);

    MessageResponse deleteReview(Long userId, Long reviewId);

    MessageResponse deleteReviewByAdmin(Long reviewId);
}
