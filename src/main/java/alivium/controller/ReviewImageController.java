package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ReviewImageResponse;
import alivium.service.ReviewImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/review-images")
@RequiredArgsConstructor
@Validated
public class ReviewImageController {

    private final ReviewImageService reviewImageService;

    @PostMapping("/upload")
    public ResponseEntity<ReviewImageResponse> uploadReviewImage(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        ReviewImageResponse response = reviewImageService.uploadReviewImage(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<ReviewImageResponse>> getReviewImages(@PathVariable Long reviewId) {
        List<ReviewImageResponse> images = reviewImageService.getReviewImages(reviewId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{imageId}/download-url")
    public ResponseEntity<String> getImageDownloadUrl(@PathVariable Long imageId) {
        String url = reviewImageService.getImageDownloadUrl(imageId);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/{imageId}/download")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable Long imageId) {
        InputStream inputStream = reviewImageService.downloadReviewImage(imageId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"review-image.jpg\"")
                .body(new InputStreamResource(inputStream));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<MessageResponse> deleteReviewImage(
            @AuthenticationPrincipal User user,
            @PathVariable Long imageId) {
        MessageResponse response = reviewImageService.deleteReviewImage(user.getId(), imageId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{imageId}/admin")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> deleteReviewImageByAdmin(@PathVariable Long imageId) {
        MessageResponse response = reviewImageService.deleteReviewImageByAdmin(imageId);
        return ResponseEntity.ok(response);
    }
}