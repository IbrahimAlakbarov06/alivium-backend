package alivium.mapper;

import alivium.domain.entity.*;
import alivium.domain.repository.CategoryRepository;
import alivium.domain.repository.CollectionRepository;
import alivium.exception.NotFoundException;
import alivium.model.dto.request.ProductCreateRequest;
import alivium.model.dto.request.ProductUpdateRequest;
import alivium.model.dto.response.ProductCategoryResponse;
import alivium.model.dto.response.ProductCollectionResponse;
import alivium.model.dto.response.ProductImageMinimalResponse;
import alivium.model.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ProductVariantMapper productVariantMapper;
    private final CategoryRepository categoryRepository;
    private final CollectionRepository collectionRepository;

    public Product toEntity(ProductCreateRequest request) {
        if (request == null) {
            return null;
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .active(request.getActive() != null ? request.getActive() : false)
                .build();

        product.setCategories(new HashSet<>());
        product.setCollections(new HashSet<>());
        product.setVariants(new HashSet<>());
        product.setImages(new HashSet<>());

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
                .variants(product.getVariants().stream()
                        .map(productVariantMapper::toResponse)
                        .collect(Collectors.toSet()))
                .images(mapProductImage(product.getImages()))
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

    public void setProductRelationsFromRequest(ProductCreateRequest request, Product product) {
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<Category> categories = request.getCategoryIds().stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElseThrow(() -> new NotFoundException("Category not found with id: " + id)))
                    .collect(Collectors.toSet());
            product.setCategories(categories);
        }

        if (request.getCollectionIds() != null && !request.getCollectionIds().isEmpty()) {
            Set<Collection> collections = request.getCollectionIds().stream()
                    .map(id -> collectionRepository.findById(id)
                            .orElseThrow(() -> new NotFoundException("Collection not found with id: " + id)))
                    .collect(Collectors.toSet());
            product.setCollections(collections);
        }

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            Set<ProductVariant> variants = request.getVariants().stream()
                    .map(variantRequest -> {
                        ProductVariant variant = productVariantMapper.toEntity(variantRequest);
                        variant.setProduct(product);
                        return variant;
                    })
                    .collect(Collectors.toSet());

            product.getVariants().addAll(variants);
        }
    }

    private Set<ProductCategoryResponse> mapCategories(Set<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return new HashSet<>();
        }
        return categories.stream()
                .map(c -> ProductCategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<ProductCollectionResponse> mapCollections(Set<Collection> collections) {
        if (collections == null || collections.isEmpty()) {
            return new HashSet<>();
        }

        return collections.stream()
                .map(c -> ProductCollectionResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<ProductImageMinimalResponse> mapProductImage(Set<ProductImage> productImages){
        if(productImages==null || productImages.isEmpty()){
            return new HashSet<>();
        }

        return productImages.stream()
                .map(i-> ProductImageMinimalResponse.builder()
                        .imageKey(i.getImageKey())
                        .id(i.getId())
                        .imageUrl(i.getImageUrl())
                        .build())
                .collect(Collectors.toSet());
    }

    public List<ProductResponse> toListResponse(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}