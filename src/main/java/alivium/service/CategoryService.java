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
import java.util.stream.Collectors;

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
            category.setParent(findById(request.getParentId()));
        }else if(request.getParentId() == null) {
            category.setParent(null);
        }

        Category updatedCategory =categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public MessageResponse deleteCategory(Long id) {
        Category category = findById(id);

        List<Category> children = categoryRepository.findActiveSubCategoriesByParentId(id);
        if (!children.isEmpty()) {
            throw new AlreadyExistsException("Cannot delete category with sub-categories");
        }

        categoryRepository.delete(category);
        return new MessageResponse("Category deleted successfully");
    }

    @Cacheable(value = "categories", key = "'all'")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toListResponseWithChildren(categoryRepository.findAll());
    }

    @Cacheable(value = "categories", key = "'active-all'")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllActiveCategories() {
        return categoryMapper.toListResponseWithChildren(categoryRepository.findByActiveTrue());
    }

    @Cacheable(value = "categories", key = "'active-main'")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveMainCategories() {
        List<Category> allActiveCategories = categoryRepository.findActiveMainCategories();

        return categoryMapper.toSimpleListResponse(allActiveCategories);
    }

    @Cacheable(value = "categories", key = "'active-sub-' + #parentId")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveSubCategories(Long parentId) {
        if (!categoryRepository.existsById(parentId)) {
            throw new NotFoundException("Parent category not found");
        }

        List<Category> allActiveCategories = categoryRepository.findByActiveTrue();

        return categoryMapper.toSubCategoriesResponseWithChildren(parentId, allActiveCategories);
    }

    @Cacheable(value = "categories", key = "#id")
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = findById(id);
        return categoryMapper.toResponseWithChildren(category, categoryRepository.findAll());
    }

    @Cacheable(value = "categories", key = "'name-' + #name")
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Category not found: " + name));
        return categoryMapper.toResponseWithChildren(category, categoryRepository.findAll());
    }

    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public CategoryResponse toggleCategoryStatus(Long id) {
        Category category = findById(id);
        category.setActive(!category.getActive());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    private Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }
}