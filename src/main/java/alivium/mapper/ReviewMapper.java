package alivium.mapper;

import alivium.domain.entity.Review;
import alivium.model.dto.request.ReviewRequest;
import alivium.model.dto.request.ReviewUpdateRequest;
import alivium.model.dto.response.ReviewImageResponse;
import alivium.model.dto.response.ReviewResponse;
import org.springframework.stereotype.Component;

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
                .build();
    }

    public ReviewResponse toResponse(Review review){
        if (review == null) return null;

        List<ReviewImageResponse> images = review.getImages().stream()
                .map(img -> ReviewImageResponse.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .imageKey(img.getImageKey())
                        .createdAt(img.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .userProfileImage(review.getUser().getProfileImage())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .images(images)
                .build();
    }

    public void updateReviewFromRequest(Review review, ReviewUpdateRequest request) {
        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getComment() != null) review.setComment(request.getComment());
    }

    public List<ReviewResponse> toListResponse(List<Review> reviews){
        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
