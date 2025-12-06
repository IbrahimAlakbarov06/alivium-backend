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
        if (request == null) return null;

        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    public CategoryResponse toResponse(Category category) {
        if (category == null) return null;

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .active(category.getActive())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .subCategories(Collections.emptyList())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public CategoryResponse toResponseWithChildren(Category category, List<Category> allCategories) {
        if (category == null) return null;

        List<Category> children = allCategories.stream()
                .filter(c -> c.getParent() != null && c.getParent().getId().equals(category.getId()))
                .collect(Collectors.toList());

        List<CategoryResponse> childResponses = children.stream()
                .map(child -> toResponseWithChildren(child, allCategories))
                .collect(Collectors.toList());

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .active(category.getActive())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .subCategories(childResponses)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
    public List<CategoryResponse> toListResponseWithChildren(List<Category> allCategories) {
        if (allCategories == null) return Collections.emptyList();

        return allCategories.stream()
                .filter(c -> c.getParent() == null)
                .map(cat -> toResponseWithChildren(cat, allCategories))
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> toSubCategoriesResponseWithChildren(Long parentId, List<Category> allCategories) {
        if (allCategories == null) return Collections.emptyList();

        return allCategories.stream()
                .filter(c -> c.getParent() != null && c.getParent().getId().equals(parentId))
                .map(cat -> toResponseWithChildren(cat, allCategories))
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> toSimpleListResponse(List<Category> categories) {
        if (categories == null) return Collections.emptyList();

        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateCategoryFromRequest(Category category, CategoryRequest request) {
        if (request.getName() != null) category.setName(request.getName());
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getImageUrl() != null) category.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) category.setActive(request.getActive());
    }
}