package alivium.controller;

import alivium.domain.entity.ProductImage;
import alivium.model.dto.request.ProductImageRequest;
import alivium.model.dto.response.ProductImageResponse;
import alivium.service.ProductImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
@Validated
public class ProductImageController {

    private final ProductImageService productImageService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImageResponse>> getImagesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getProductImagesByProductId(productId));
    }

    @GetMapping("/{imageId}/download-url")
    public ResponseEntity<String> getImageDownloadUrl(@PathVariable Long imageId) {
        return ResponseEntity.ok(productImageService.getImageDownloadUrl(imageId));
    }

    @GetMapping("/{imageId}/download")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable Long imageId) {
        ProductImage image = productImageService.findById(imageId);
        InputStream inputStream = productImageService.downloadProductImage(imageId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + image.getImageKey() + "\"")
                .body(new InputStreamResource(inputStream));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ProductImageResponse> uploadImage(@Valid ProductImageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productImageService.uploadProductImage(request));
    }

    @PutMapping("/{imageId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ProductImageResponse> updateImage(
            @PathVariable Long imageId,
            @RequestParam("file") MultipartFile newFile) {
        return ResponseEntity.ok(productImageService.updateProductImage(imageId, newFile));
    }

    @PatchMapping("/{imageId}/set-primary")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ProductImageResponse> setPrimaryImage(@PathVariable Long imageId) {
        return ResponseEntity.ok(productImageService.setPrimaryImage(imageId));
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteProductImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
