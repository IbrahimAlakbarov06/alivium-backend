package alivium.mapper;

import alivium.domain.entity.ProductVariant;
import alivium.model.dto.request.ProductVariantRequest;
import alivium.model.dto.response.ProductVariantResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductVariantMapper {

    public ProductVariant toEntity(ProductVariantRequest request) {
        if (request == null) return null;

        return ProductVariant.builder()
                .color(request.getColor())
                .size(request.getSize())
                .stockQuantity(request.getStockQuantity())
                .sku(request.getSku())
                .additionalPrice(request.getAdditionalPrice())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .build();
    }

    public ProductVariantResponse toResponse(ProductVariant variant) {
        if (variant == null) return null;

        return ProductVariantResponse.builder()
                .id(variant.getId())
                .color(variant.getColor())
                .size(variant.getSize())
                .stockQuantity(variant.getStockQuantity())
                .sku(variant.getSku())
                .additionalPrice(variant.getAdditionalPrice())
                .available(variant.getAvailable())
                .productId(variant.getProduct() != null ? variant.getProduct().getId() : null)
                .productName(variant.getProduct() != null ? variant.getProduct().getName() : null)
                .build();
    }

    public List<ProductVariantResponse> toListResponse(List<ProductVariant> variants) {
        if (variants == null) return List.of();

        return variants.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateVariantFromRequest(ProductVariant variant, ProductVariantRequest request) {
        if (request.getColor() != null) variant.setColor(request.getColor());
        if (request.getSize() != null) variant.setSize(request.getSize());
        if (request.getStockQuantity() != null) variant.setStockQuantity(request.getStockQuantity());
        if (request.getSku() != null) variant.setSku(request.getSku());
        if (request.getAdditionalPrice() != null) variant.setAdditionalPrice(request.getAdditionalPrice());
        if (request.getAvailable() != null) variant.setAvailable(request.getAvailable());
    }
}