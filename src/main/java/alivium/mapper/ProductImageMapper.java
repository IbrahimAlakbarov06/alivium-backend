package alivium.mapper;

import alivium.domain.entity.Product;
import alivium.domain.entity.ProductImage;
import alivium.model.dto.request.ProductImageRequest;
import alivium.model.dto.response.ProductImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductImageMapper {

    public ProductImage toEntity(ProductImageRequest request, Product product) {
        if (request == null || product == null) return null;

        return ProductImage.builder()
                .product(product)
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
                .build();
    }

    public ProductImageResponse toResponse(ProductImage entity) {
        if (entity == null) return null;

        return ProductImageResponse.builder()
                .id(entity.getId())
                .imageUrl(entity.getImageUrl())
                .imageKey(entity.getImageKey())
                .isPrimary(entity.getIsPrimary())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
