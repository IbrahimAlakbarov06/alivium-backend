package alivium.mapper;


import alivium.domain.entity.*;
import alivium.model.dto.request.ProductCreateRequest;
import alivium.model.dto.request.ProductUpdateRequest;
import alivium.model.dto.response.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toEntity(ProductCreateRequest request) {
        if (request == null) {
            return null;
        }

        Product product =  Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .active(request.getActive()!=null? request.getActive():false)
                .build();

        product.setCategories(Collections.emptySet());
        product.setCollections(Collections.emptySet());
        product.setVariants(Collections.emptySet());
        product.setImages(Collections.emptySet());

        return product;
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .active(product.getActive())
                .categories(mapCategories(product.getCategories()))
                .collections(mapCollections(product.getCollections()))
                .variants(mapVariants(product.getVariants()))
                .images(mapImages(product.getImages()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(ProductUpdateRequest request, Product product) {
        if (request == null || product == null) return;

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getDiscountPrice() != null) product.setDiscountPrice(request.getDiscountPrice());
        if (request.getActive() != null) product.setActive(request.getActive());
    }

    //helper
    private Set<ProductCategoryResponse> mapCategories(Set<Category> categories) {
        if (categories == null) return Collections.emptySet();
        return categories.stream()
                .map(c -> ProductCategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .collect(Collectors.toSet());
    }

    //helper
    private Set<ProductCollectionResponse> mapCollections(Set<Collection> collections) {
        if (collections == null) return Collections.emptySet();

        return collections.stream()
                .map(c -> ProductCollectionResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<ProductVariantResponse> mapVariants(Set<ProductVariant> variants) {
        if (variants == null) return Collections.emptySet();
        return variants.stream()
                .map(v -> ProductVariantResponse.builder()
                        .id(v.getId())
                        .color(v.getColor())
                        .size(v.getSize())
                        .stockQuantity(v.getStockQuantity())
                        .sku(v.getSku())
                        .additionalPrice(v.getAdditionalPrice())
                        .available(v.getAvailable())
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<ProductImageResponse> mapImages(Set<ProductImage> images) {
        if (images == null) return Collections.emptySet();
        return images.stream()
                .map(i -> ProductImageResponse.builder()
                        .id(i.getId())
                        .imageUrl(i.getImageUrl())
                        .build())
                .collect(Collectors.toSet());
    }


}
