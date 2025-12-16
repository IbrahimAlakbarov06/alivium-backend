package alivium.controller;

import alivium.model.dto.request.ProductCreateRequest;
import alivium.model.dto.request.ProductUpdateRequest;
import alivium.model.dto.response.ProductResponse;
import alivium.service.ProductService;
import alivium.service.impl.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse updated = productService.updateProduct(productId, request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{productId}/discount")
    public ResponseEntity<ProductResponse> updateDiscount(
            @PathVariable Long productId,
            @RequestParam BigDecimal discountPrice) {
        ProductResponse updated = productService.updateProductDiscountPrice(productId, discountPrice);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{productId}/status")
    public ResponseEntity<ProductResponse> switchStatus(@PathVariable Long productId) {
        ProductResponse updated = productService.switchProductStatus(productId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {
        return ResponseEntity.ok(productService.getActiveProducts());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }

    @GetMapping("/collection/{collectionId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCollection(@PathVariable Long collectionId) {
        return ResponseEntity.ok(productService.getProductsByCollectionId(collectionId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
