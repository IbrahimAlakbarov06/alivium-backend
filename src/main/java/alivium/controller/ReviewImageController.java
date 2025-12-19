package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ReviewImageResponse;
import alivium.service.ReviewImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        String filename = reviewImageService.findById(imageId).getImageKey();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(new InputStreamResource(inputStream));
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<ReviewImageResponse> uploadReviewImage(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file,
            @RequestParam("reviewId") Long reviewId) {
        ReviewImageResponse response = reviewImageService.uploadReviewImage(file, reviewId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<MessageResponse> deleteReviewImage(
            @AuthenticationPrincipal User user,
            @PathVariable Long imageId) {
        MessageResponse response = reviewImageService.deleteReviewImage(user.getId(), imageId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{imageId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ReviewImageResponse> updateImage(
            @PathVariable Long imageId,
            @RequestParam("file") MultipartFile newFile) {
        return ResponseEntity.ok(reviewImageService.updateReviewImage(imageId, newFile));
    }

    @DeleteMapping("/{imageId}/admin")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> deleteReviewImageByAdmin(@PathVariable Long imageId) {
        MessageResponse response = reviewImageService.deleteReviewImageByAdmin(imageId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{imageId}/refresh-url")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<ReviewImageResponse> refreshImageUrl(@PathVariable Long imageId) {
        ReviewImageResponse response = reviewImageService.refreshUrl(imageId);
        return ResponseEntity.ok(response);
    }
}
