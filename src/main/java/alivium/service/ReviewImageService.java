package alivium.service;

import alivium.domain.entity.ReviewImage;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ReviewImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ReviewImageService {
    ReviewImageResponse uploadReviewImage(MultipartFile file, Long reviewId);

    List<ReviewImageResponse> getReviewImages(Long reviewId);

    String getImageDownloadUrl(Long imageId);

    InputStream downloadReviewImage(Long imageId);

    MessageResponse deleteReviewImage(Long userId, Long imageId);

    MessageResponse deleteReviewImageByAdmin(Long imageId);

    ReviewImageResponse refreshUrl(Long imageId);

    ReviewImage findById(Long imageId);

    ReviewImageResponse updateReviewImage(Long imageId, MultipartFile newFile);
}
