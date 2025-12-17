package alivium.service;

import alivium.model.dto.request.CategoryRequest;
import alivium.model.dto.response.CategoryResponse;
import alivium.model.dto.response.MessageResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    MessageResponse deleteCategory(Long id);

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getAllActiveCategories();

    List<CategoryResponse> getActiveMainCategories();

    List<CategoryResponse> getActiveSubCategories(Long parentId);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse getCategoryByName(String name);

    CategoryResponse toggleCategoryStatus(Long id);
}
