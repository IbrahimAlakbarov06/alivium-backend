package alivium.mapper;

import alivium.domain.entity.Collection;
import alivium.model.dto.request.CollectionRequest;
import alivium.model.dto.response.CollectionResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CollectionMapper {

    public Collection toEntity(CollectionRequest request) {
        if (request == null) return null;

        return Collection.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .bannerImageUrl(request.getBannerUrl())
                .isActive(request.getActive() != null ? request.getActive() : true)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();
    }

    public CollectionResponse toResponse(Collection collection) {
        if (collection == null) return null;

        return CollectionResponse.builder()
                .id(collection.getId())
                .name(collection.getName())
                .description(collection.getDescription())
                .type(collection.getType())
                .bannerUrl(collection.getBannerImageUrl())
                .active(collection.getIsActive())
                .displayOrder(collection.getDisplayOrder())
                .productCount(collection.getProducts() != null ? collection.getProducts().size() : 0)
                .startDate(collection.getStartDate())
                .endDate(collection.getEndDate())
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .build();
    }

    public List<CollectionResponse> toListResponse(List<Collection> collections) {
        if (collections == null) return List.of();

        return collections.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateCollectionFromRequest(Collection collection, CollectionRequest request) {
        if (request.getName() != null) collection.setName(request.getName());
        if (request.getDescription() != null) collection.setDescription(request.getDescription());
        if (request.getType() != null) collection.setType(request.getType());
        if (request.getBannerUrl() != null) collection.setBannerImageUrl(request.getBannerUrl());
        if (request.getActive() != null) collection.setIsActive(request.getActive());
        if (request.getStartDate() != null) collection.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) collection.setEndDate(request.getEndDate());
        if (request.getDisplayOrder() != null) collection.setDisplayOrder(request.getDisplayOrder());
    }
}