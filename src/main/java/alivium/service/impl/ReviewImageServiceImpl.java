package alivium.service.impl;

import alivium.config.MinioProperties;
import alivium.domain.entity.Review;
import alivium.domain.entity.ReviewImage;
import alivium.domain.repository.ReviewImageRepository;
import alivium.domain.repository.ReviewRepository;
import alivium.exception.BusinessException;
import alivium.exception.NotFoundException;
import alivium.mapper.ReviewImageMapper;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ReviewImageResponse;
import alivium.service.ReviewImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewImageServiceImpl implements ReviewImageService {

    private final ReviewImageRepository reviewImageRepository;
    private final ReviewRepository reviewRepository;
    private final MinioImageStorageService imageStorage;
    private final MinioProperties minioProperties;
    private final ReviewImageMapper reviewImageMapper;

    @Transactional
    @CacheEvict(value = {"reviewImages","reviews"}, allEntries = true)
    public ReviewImageResponse uploadReviewImage(MultipartFile file, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found with id: " + reviewId));

        String imageKey = imageStorage.uploadFile(file, reviewBucket());
        String imageUrl = imageStorage.getPreSignedUrl(reviewBucket(), imageKey, 3600);

        ReviewImage image = ReviewImage.builder()
                .review(review)
                .imageUrl(imageUrl)
                .imageKey(imageKey)
                .imageUrlExpiry(LocalDateTime.now().plusSeconds(3600))
                .build();

        ReviewImage saved = reviewImageRepository.save(image);
        return reviewImageMapper.toResponse(saved);
    }

    @Transactional
    @Cacheable(value = "reviewImages", key = "#reviewId")
    public List<ReviewImageResponse> getReviewImages(Long reviewId) {
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found with id: " + reviewId));

        List<ReviewImage> images = reviewImageRepository.findByReviewId(reviewId);
        return images.stream()
                .map(img -> {
                    img.setImageUrl(getValidImageUrl(img));
                    return reviewImageMapper.toResponse(img);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public String getImageDownloadUrl(Long imageId) {
        ReviewImage image = findById(imageId);
        return getValidImageUrl(image);
    }

    @Transactional(readOnly = true)
    public InputStream downloadReviewImage(Long imageId) {
        ReviewImage image = findById(imageId);
        return imageStorage.downloadFile(reviewBucket(), image.getImageKey());
    }

    @Transactional
    @CacheEvict(value = {"reviewImages","reviews"}, allEntries = true)
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
    @CacheEvict(value ={"reviewImages","reviews"}, allEntries = true)
    public MessageResponse deleteReviewImageByAdmin(Long imageId) {
        ReviewImage image = findById(imageId);
        deleteImageFromStorage(image);
        reviewImageRepository.delete(image);

        return new MessageResponse("Review image deleted successfully by admin");
    }

    @Transactional
    @CacheEvict(value ={"reviewImages","reviews"}, allEntries = true)
    public ReviewImageResponse refreshUrl(Long imageId) {
        ReviewImage image = findById(imageId);
        String url = imageStorage.getPreSignedUrl(reviewBucket(), image.getImageKey(), 3600);
        image.setImageUrl(url);
        image.setImageUrlExpiry(LocalDateTime.now().plusSeconds(3600));
        ReviewImage saved = reviewImageRepository.save(image);
        return reviewImageMapper.toResponse(saved);
    }

    @Transactional
    public String getValidImageUrl(ReviewImage image) {
        if (image.getImageUrl() == null || image.getImageUrlExpiry() == null
                || image.getImageUrlExpiry().isBefore(LocalDateTime.now())) {
            String newUrl = imageStorage.getPreSignedUrl(reviewBucket(), image.getImageKey(), 3600);
            image.setImageUrl(newUrl);
            LocalDateTime newExpiry = LocalDateTime.now().plusSeconds(3600);
            image.setImageUrlExpiry(newExpiry);
            updateImageUrlAsync(image.getId(), newUrl, newExpiry);
        }
        return image.getImageUrl();
    }

    @Async
    @Transactional
    public void updateImageUrlAsync(Long imageId, String newUrl, LocalDateTime newExpiry) {
        ReviewImage image = findById(imageId);
        image.setImageUrl(newUrl);
        image.setImageUrlExpiry(newExpiry);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"reviewImages","reviews"}, allEntries = true)
    public ReviewImageResponse updateReviewImage(Long imageId, MultipartFile newFile) {
        ReviewImage existing = findById(imageId);

        deleteImageFromStorage(existing);

        String key = imageStorage.uploadFile(newFile, reviewBucket());

        existing.setImageKey(key);
        existing.setImageUrl(null);
        existing.setImageUrlExpiry(null);

        existing.setImageUrl(imageStorage.getPreSignedUrl(reviewBucket(), key, 3600));
        ReviewImage saved = reviewImageRepository.save(existing);

        return reviewImageMapper.toResponse(saved);
    }

    private void deleteImageFromStorage(ReviewImage image) {
        imageStorage.deleteFile(reviewBucket(), image.getImageKey());
    }

    public ReviewImage findById(Long imageId) {
        return reviewImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Review image not found with id: " + imageId));
    }

    private String reviewBucket() {
        return minioProperties.getBucket().getReview();
    }
}
