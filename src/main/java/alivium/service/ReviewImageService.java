package alivium.service;

import alivium.config.MinioProperties;
import alivium.domain.entity.Review;
import alivium.domain.entity.ReviewImage;
import alivium.domain.repository.ReviewImageRepository;
import alivium.domain.repository.ReviewRepository;
import alivium.exception.BusinessException;
import alivium.exception.NotFoundException;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ReviewImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewImageService {

    private final ReviewImageRepository reviewImageRepository;
    private final ReviewRepository reviewRepository;
    private final MinioService minioService;
    private final MinioProperties minioProperties;

    @Transactional
    public ReviewImageResponse uploadReviewImage(MultipartFile file) {
        String imageKey = minioService.uploadFile(file, reviewBucket());
        String imageUrl = minioService.getPreSignedUrl(reviewBucket(), imageKey, 3600);

        ReviewImageResponse response = ReviewImageResponse.builder()
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .build();

        return response;
    }

    @Transactional(readOnly = true)
    public List<ReviewImageResponse> getReviewImages(Long reviewId) {
        List<ReviewImage> images = reviewImageRepository.findByReviewId(reviewId);
        return images.stream()
                .map(img -> ReviewImageResponse.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .imageKey(img.getImageKey())
                        .createdAt(img.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public String getImageDownloadUrl(Long imageId) {
        ReviewImage image = findById(imageId);
        return minioService.getPreSignedUrl(reviewBucket(), image.getImageKey(), 3600);
    }

    @Transactional(readOnly = true)
    public InputStream downloadReviewImage(Long imageId) {
        ReviewImage image = findById(imageId);
        return minioService.downloadFile(reviewBucket(), image.getImageKey());
    }

    @Transactional
    public MessageResponse deleteReviewImage(Long userId, Long imageId) {
        ReviewImage image = findById(imageId);
        Review review = image.getReview();

        if (!review.getUser().getId().equals(userId)) {
            throw new BusinessException("You can only delete images from your own reviews");
        }

        deleteImageFromStorage(image);
        reviewImageRepository.delete(image);

        return new MessageResponse("Review image deleted successfully");
    }

    @Transactional
    public MessageResponse deleteReviewImageByAdmin(Long imageId) {
        ReviewImage image = findById(imageId);

        deleteImageFromStorage(image);
        reviewImageRepository.delete(image);

        return new MessageResponse("Review image deleted successfully by admin");
    }

    private void deleteImageFromStorage(ReviewImage image) {
        try {
            minioService.deleteFile(reviewBucket(), image.getImageKey());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from storage: " + e.getMessage());
        }
    }

    private ReviewImage findById(Long imageId) {
        return reviewImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Review image not found with id: " + imageId));
    }

    private String reviewBucket() {
        return minioProperties.getBucket().getReview();
    }

}