package alivium.service.impl;

import alivium.config.MinioProperties;
import alivium.domain.entity.Product;
import alivium.domain.entity.Review;
import alivium.domain.entity.ReviewImage;
import alivium.domain.entity.User;
import alivium.domain.repository.ProductRepository;
import alivium.domain.repository.ReviewImageRepository;
import alivium.domain.repository.ReviewRepository;
import alivium.domain.repository.UserRepository;
import alivium.exception.AlreadyExistsException;
import alivium.exception.BusinessException;
import alivium.exception.NotFoundException;
import alivium.mapper.ReviewMapper;
import alivium.model.dto.request.ReviewRequest;
import alivium.model.dto.request.ReviewUpdateRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ProductRatingResponse;
import alivium.model.dto.response.ReviewResponse;
import alivium.service.MinioService;
import alivium.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final MinioService minioService;
    private final MinioProperties minioProperties;

    @Transactional
    @CacheEvict(value = {"reviews", "products"}, allEntries = true)
    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        User user = findUserById(userId);
        Product product = findProductById(request.getProductId());

        if (reviewRepository.existsByUserIdAndProductId(userId, request.getProductId())) {
            throw new AlreadyExistsException("You have already reviewed this product");
        }

        Review review = reviewMapper.toEntity(request);
        review.setUser(user);
        review.setProduct(product);

        Review savedReview = reviewRepository.save(review);

        if (request.getImageKeys() != null && !request.getImageKeys().isEmpty()) {
            for (String imageKey : request.getImageKeys()) {
                String imageUrl = minioService.getPreSignedUrl(reviewBucket(), imageKey, 3600);
                ReviewImage reviewImage = ReviewImage.builder()
                        .review(savedReview)
                        .imageKey(imageKey)
                        .imageUrl(imageUrl)
                        .build();
                reviewImageRepository.save(reviewImage);
            }
        }

        updateProductRating(request.getProductId());

        Review reviewWithImages = reviewRepository.findByIdWithImages(savedReview.getId())
                .orElseThrow(()-> new NotFoundException("Review not found"));
        return reviewMapper.toResponse(reviewWithImages);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "reviews", key = "'user-' + #userId")
    public List<ReviewResponse> getUserReviews(Long userId) {
        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return reviewMapper.toListResponse(reviews);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "reviews", key = "'product-' + #productId")
    public List<ReviewResponse> getProductReviews(Long productId) {
        List<Review> reviews = reviewRepository.findByProductIdAndActiveTrueOrderByCreatedAtDesc(productId);
        return reviewMapper.toListResponse(reviews);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "reviews", key = "'rating-' + #productId")
    public ProductRatingResponse getProductRatingStats(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found with id: " + productId);
        }

        Double averageRating = reviewRepository.calculateRating(productId);
        Long totalReviews = reviewRepository.countByProductId(productId);

        Long fiveStar = (long) reviewRepository.findByProductIdAndRatingAndActiveTrue(productId, 5).size();
        Long fourStar = (long) reviewRepository.findByProductIdAndRatingAndActiveTrue(productId, 4).size();
        Long threeStar = (long) reviewRepository.findByProductIdAndRatingAndActiveTrue(productId, 3).size();
        Long twoStar = (long) reviewRepository.findByProductIdAndRatingAndActiveTrue(productId, 2).size();
        Long oneStar = (long) reviewRepository.findByProductIdAndRatingAndActiveTrue(productId, 1).size();

        return reviewMapper.toRatingResponse(productId, averageRating, totalReviews,
                fiveStar, fourStar, threeStar, twoStar, oneStar);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "reviews", key = "#reviewId")
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = findById(reviewId);
        return reviewMapper.toResponse(review);
    }


    @Transactional
    @CacheEvict(value = {"reviews", "products"}, allEntries = true)
    public ReviewResponse updateReview(Long userId, Long reviewId, ReviewUpdateRequest request) {
        Review review = findById(reviewId);

        if (!review.getUser().getId().equals(userId)) {
            throw new BusinessException("You can only update your own review");
        }

        reviewMapper.updateReviewFromRequest(review, request);
        Review updated=reviewRepository.save(review);

        if (request.getRating() != null) {
            updateProductRating(review.getProduct().getId());
        }

        return reviewMapper.toResponse(updated);
    }


    @Transactional
    @CacheEvict(value = {"reviews", "products"}, allEntries = true)
    public ReviewResponse toggleReviewStatus(Long reviewId){
        Review review = findById(reviewId);
        review.setActive(!review.getActive());

        updateProductRating(review.getProduct().getId());
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    @CacheEvict(value = {"reviews", "products"}, allEntries = true)
    public ReviewResponse verifyReview(Long reviewId){
        Review review = findById(reviewId);
        review.setActive(true);
        return reviewMapper.toResponse(reviewRepository.save(review));
    }


    @Transactional
    @CacheEvict(value = {"reviews", "products"}, allEntries = true)
    public MessageResponse deleteReview(Long userId, Long reviewId) {
        Review review = findById(reviewId);

        if (!review.getUser().getId().equals(userId)) {
            throw new BusinessException("You can only delete your own reviews");
        }

        Long productId = review.getProduct().getId();
        deleteReviewImages(review);
        reviewRepository.delete(review);

        updateProductRating(productId);
        return new MessageResponse("Review deleted successfully");
    }

    @Transactional
    @CacheEvict(value = {"reviews", "products"}, allEntries = true)
    public MessageResponse deleteReviewByAdmin(Long reviewId) {
        Review review = findById(reviewId);
        Long productId = review.getProduct().getId();

        deleteReviewImages(review);
        reviewRepository.delete(review);

        updateProductRating(productId);
        return new MessageResponse("Review deleted successfully by admin");
    }
    private void deleteReviewImages(Review review) {
        List<ReviewImage> images = reviewImageRepository.findByReviewId(review.getId());
        for (ReviewImage image : images) {
            try {
                minioService.deleteFile(reviewBucket(), image.getImageKey());
            } catch (Exception e) {

            }
        }
    }


    private void updateProductRating(Long productId){
        Product product = findProductById(productId);

        Double averageRating = reviewRepository.calculateRating(productId);
        Long reviewCount = reviewRepository.countByProductId(productId);

        product.setAverageRating(averageRating != null ? averageRating : 0.0);
        product.setReviewCount(reviewCount != null ? reviewCount.intValue() : 0);

        productRepository.save(product);
    }

    private Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found with id: " + reviewId));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("User not found with id: " + userId));
    }

    private String reviewBucket() {
        return minioProperties.getBucket().getReview();
    }
}
