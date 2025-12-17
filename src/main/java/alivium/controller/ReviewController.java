package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.request.ReviewRequest;
import alivium.model.dto.request.ReviewUpdateRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ProductRatingResponse;
import alivium.model.dto.response.ReviewResponse;
import alivium.service.ReviewService;
import alivium.service.impl.ReviewServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/me")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@AuthenticationPrincipal User user) {
        List<ReviewResponse> reviews = reviewService.getUserReviews(user.getId());
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(@PathVariable Long userId) {
        List<ReviewResponse> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable Long productId) {
        List<ReviewResponse> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<ProductRatingResponse> getProductReviewsByRating(@PathVariable Long productId) {
        ProductRatingResponse ratingStat = reviewService.getProductRatingStats(productId);
        return ResponseEntity.ok(ratingStat);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long reviewId) {
        ReviewResponse review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request) {
        ReviewResponse response = reviewService.updateReview(user.getId(), reviewId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reviewId}/verify")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<ReviewResponse> verifyReview(@PathVariable Long reviewId) {
        ReviewResponse response = reviewService.verifyReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reviewId}/toggle-status")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<ReviewResponse> toggleReviewStatus(@PathVariable Long reviewId) {
        ReviewResponse response = reviewService.toggleReviewStatus(reviewId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<MessageResponse> deleteReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId) {
        MessageResponse response = reviewService.deleteReview(user.getId(), reviewId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}/admin")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> deleteReviewByAdmin(@PathVariable Long reviewId) {
        MessageResponse response = reviewService.deleteReviewByAdmin(reviewId);
        return ResponseEntity.ok(response);
    }
}