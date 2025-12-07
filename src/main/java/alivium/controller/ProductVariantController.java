package alivium.controller;

import alivium.model.dto.request.ProductVariantRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ProductVariantResponse;
import alivium.service.ProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class ProductVariantController {

    private final ProductVariantService variantService;

    @GetMapping("/products/{productId}/variants")
    public ResponseEntity<List<ProductVariantResponse>> getVariantsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(variantService.getVariantsByProduct(productId));
    }

    @GetMapping("/products/{productId}/variants/available")
    public ResponseEntity<List<ProductVariantResponse>> getAvailableVariants(@PathVariable Long productId) {
        return ResponseEntity.ok(variantService.getAvailableVariantsByProduct(productId));
    }

    @GetMapping("/products/{productId}/variants/color/{color}")
    public ResponseEntity<List<ProductVariantResponse>> getVariantsByColor(
            @PathVariable Long productId,
            @PathVariable String color) {
        return ResponseEntity.ok(variantService.getVariantsByColor(productId, color));
    }

    @GetMapping("/products/{productId}/variants/size/{size}")
    public ResponseEntity<List<ProductVariantResponse>> getVariantsBySize(
            @PathVariable Long productId,
            @PathVariable String size) {
        return ResponseEntity.ok(variantService.getVariantsBySize(productId, size));
    }

    @GetMapping("/variants/{variantId}")
    public ResponseEntity<ProductVariantResponse> getVariantById(@PathVariable Long variantId) {
        return ResponseEntity.ok(variantService.getVariantById(variantId));
    }

    @GetMapping("/variants/sku/{sku}")
    public ResponseEntity<ProductVariantResponse> getVariantBySku(@PathVariable String sku) {
        return ResponseEntity.ok(variantService.getVariantBySku(sku));
    }

    @PostMapping("/products/{productId}/variants")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<ProductVariantResponse> createVariant(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(variantService.addVariantToProduct(productId, request));
    }

    @PutMapping("/variants/{variantId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<ProductVariantResponse> updateVariant(
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(variantService.updateVariant(variantId, request));
    }

    @PatchMapping("/variants/{variantId}/stock")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<ProductVariantResponse> updateStock(
            @PathVariable Long variantId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(variantService.updateStock(variantId, quantity));
    }

    @PatchMapping("/variants/{variantId}/stock/increase")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<ProductVariantResponse> increaseStock(
            @PathVariable Long variantId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(variantService.increaseStock(variantId, quantity));
    }

    @PatchMapping("/variants/{variantId}/stock/decrease")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<ProductVariantResponse> decreaseStock(
            @PathVariable Long variantId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(variantService.decreaseStock(variantId, quantity));
    }

    @PatchMapping("/variants/{variantId}/toggle-availability")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<ProductVariantResponse> toggleAvailability(@PathVariable Long variantId) {
        return ResponseEntity.ok(variantService.toggleAvailability(variantId));
    }

    @DeleteMapping("/variants/{variantId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> deleteVariant(@PathVariable Long variantId) {
        return ResponseEntity.ok(variantService.deleteVariant(variantId));
    }
}