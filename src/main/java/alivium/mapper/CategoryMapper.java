package alivium.mapper;

import alivium.domain.entity.Category;
import alivium.model.dto.request.CategoryRequest;
import alivium.model.dto.response.CategoryResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }

        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .active(category.getActive() != null ? category.getActive() : true)
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .subCategories(
                        category.getSubCategories() != null && !category.getSubCategories().isEmpty()
                                ? category.getSubCategories().stream()
                                .map(this::toSimpleResponse)
                                .collect(Collectors.toList())
                                : Collections.emptyList()
                )
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public CategoryResponse toSimpleResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .active(category.getActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public void updateCategoryFromRequest(Category category,CategoryRequest request) {
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
    }

    public List<CategoryResponse> toListResponse(List<Category> categories) {
        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}
