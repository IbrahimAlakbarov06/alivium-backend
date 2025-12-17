package alivium.mapper;

import alivium.domain.entity.Review;
import alivium.model.dto.request.ReviewRequest;
import alivium.model.dto.request.ReviewUpdateRequest;
import alivium.model.dto.response.ProductRatingResponse;
import alivium.model.dto.response.ReviewImageResponse;
import alivium.model.dto.response.ReviewResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

    public Review toEntity(ReviewRequest request){
        if (request == null) return null;

        return Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .active(true)
                .verified(false)
                .images(new HashSet<>())
                .build();
    }

    public ReviewResponse toResponse(Review review){
        if (review == null) return null;

        List<ReviewImageResponse> images = (review.getImages() != null)
                ? review.getImages().stream()
                .map(img -> ReviewImageResponse.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .imageKey(img.getImageKey())
                        .createdAt(img.getCreatedAt())
                        .build())
                .collect(Collectors.toList())
                : Collections.emptyList();

        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .userProfileImage(review.getUser().getProfileImage())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .verified(review.getVerified())
                .active(review.getActive())
                .images(images)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public void updateReviewFromRequest(Review review, ReviewUpdateRequest request) {
        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getComment() != null) review.setComment(request.getComment());
    }

    public ProductRatingResponse toRatingResponse(Long productId,
                                                  Double averageRating,
                                                  Long totalReviews,
                                                  Long fiveStar,
                                                  Long fourStar,
                                                  Long threeStar,
                                                  Long twoStar,
                                                  Long oneStar) {
        return ProductRatingResponse.builder()
                .productId(productId)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .totalReviews(totalReviews)
                .fiveStarCount(fiveStar)
                .fourStarCount(fourStar)
                .threeStarCount(threeStar)
                .twoStarCount(twoStar)
                .oneStarCount(oneStar)
                .build();
    }

    public List<ReviewResponse> toListResponse(List<Review> reviews){
        if (reviews == null) return Collections.emptyList();

        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}