package alivium.service;

import alivium.domain.entity.Category;
import alivium.domain.repository.CategoryRepository;
import alivium.exception.AlreadyExistsException;
import alivium.exception.NotFoundException;
import alivium.mapper.CategoryMapper;
import alivium.model.dto.request.CategoryRequest;
import alivium.model.dto.response.CategoryResponse;
import alivium.model.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new AlreadyExistsException("Category with name " + request.getName() + " already exists");
        }

        Category category =categoryMapper.toEntity(request);

        if (request.getParentId() !=null) {
            Category parent =findById(request.getParentId());
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findById(id);

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new AlreadyExistsException("Category already exists with name: " + request.getName());
            }
        }

        categoryMapper.updateCategoryFromRequest(category, request);

        if (request.getParentId() != null) {
            Category parent =findById(request.getParentId());
            category.setParent(parent);
        }else if(request.getParentId() ==null && category.getParent() != null) {
            category.setParent(null);
        }

        Category updatedCategory =categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public MessageResponse deleteCategory(Long id) {
        Category category = findById(id);

        if (!category.getSubCategories().isEmpty()) {
            throw new AlreadyExistsException("Cannot delete category with sub-categories. Please delete sub-categories first.");
        }

        categoryRepository.delete(category);
        return new MessageResponse("Category deleted successfully");
    }

    @Cacheable(value = "categories", key = "'all'")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toListResponse(categories);
    }

    @Cacheable(value = "categories", key = "'active-all'")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllActiveCategories() {
        List<Category> categories = categoryRepository.findByActiveTrue();
        return categoryMapper.toListResponse(categories);
    }

    @Cacheable(value = "categories", key = "'active-main'")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveMainCategories() {
        List<Category> mainCategories = categoryRepository.findActiveMainCategories();
        return categoryMapper.toListResponse(mainCategories);
    }

    @Cacheable(value = "categories", key = "'active-sub-' + #parentId")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveSubCategories(Long parentId) {
        if (!categoryRepository.existsById(parentId)) {
            throw new NotFoundException("Parent category not found with id: " + parentId);
        }
        List<Category> subCategories = categoryRepository.findActiveSubCategoriesByParentId(parentId);
        return categoryMapper.toListResponse(subCategories);
    }

    @Cacheable(value = "categories", key = "#id")
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = findById(id);
        return categoryMapper.toResponse(category);
    }

    @Cacheable(value = "categories", key = "'name-' + #name")
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Category not found with name: " + name));
        return categoryMapper.toResponse(category);
    }

    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public CategoryResponse toggleCategoryStatus(Long id) {
        Category category = findById(id);
        category.setActive(!category.getActive());
        Category updated = categoryRepository.save(category);
        return categoryMapper.toResponse(updated);
    }

    private Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
    }
}
