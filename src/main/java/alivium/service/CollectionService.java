package alivium.service;

import alivium.domain.entity.Collection;
import alivium.domain.entity.Product;
import alivium.domain.repository.CollectionRepository;
import alivium.domain.repository.ProductRepository;
import alivium.exception.AlreadyExistsException;
import alivium.exception.BusinessException;
import alivium.exception.NotFoundException;
import alivium.mapper.CollectionMapper;
import alivium.model.dto.request.CollectionRequest;
import alivium.model.dto.response.CollectionResponse;
import alivium.model.dto.response.MessageResponse;
import alivium.model.enums.CollectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionService {
    private final CollectionRepository collectionRepository;
    private final CollectionMapper collectionMapper;
    private final ProductRepository productRepository;


    @Transactional
    @CacheEvict(value = "collections", allEntries = true)
    public CollectionResponse createCollection(CollectionRequest request) {
        if (collectionRepository.existsByName(request.getName())) {
            throw new AlreadyExistsException("Collection with name " + request.getName() + " already exists");
        }

        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new BusinessException("End date cannot be before start date");
            }
        }

        Collection collection = collectionMapper.toEntity(request);
        Collection saved = collectionRepository.save(collection);
        return collectionMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "collections", allEntries = true)
    public CollectionResponse updateCollection(Long collecionId, CollectionRequest request) {
        Collection collection = findById(collecionId);

        if (request.getName() != null && !request.getName().equals(collection.getName())) {
            if (collectionRepository.existsByName(request.getName())) {
                throw new AlreadyExistsException("Collection already exists with name: " + request.getName());
            }
        }

        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new BusinessException("End date cannot be before start date");
            }
        }
        collectionMapper.updateCollectionFromRequest(collection, request);
        Collection updated = collectionRepository.save(collection);

        return collectionMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "collections", key = "'all'")
    public List<CollectionResponse> getAllCollections() {
        return collectionMapper.toListResponse(collectionRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "collections", key = "'active'")
    public List<CollectionResponse> getActiveCollections() {
        return collectionMapper.toListResponse(
                collectionRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "collections", key = "'type-' + #type")
    public List<CollectionResponse> getCollectionsByType(CollectionType type) {
        return collectionMapper.toListResponse(
                collectionRepository.findActiveByTypeOrderByDisplayOrder(type)
        );
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "collections", key = "'current-active'")
    public List<CollectionResponse> getCurrentActiveCollections() {
        LocalDateTime now = LocalDateTime.now();
        return collectionMapper.toListResponse(
                collectionRepository.findActiveCollectionsInDateRange(now)
        );
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "collections", key = "#id")
    public CollectionResponse getCollectionById(Long id) {
        Collection collection = findById(id);
        return collectionMapper.toResponse(collection);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "collections", key = "'name-' + #name")
    public CollectionResponse getCollectionByName(String name) {
        Collection collection = collectionRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Collection not found: " + name));
        return collectionMapper.toResponse(collection);
    }

    @Transactional
    @CacheEvict(value = "collections", allEntries = true)
    public MessageResponse deleteCollection(Long id) {
        Collection collection = findById(id);

        collectionRepository.delete(collection);
        return new MessageResponse("Collection with id " + id + " deleted successfully");
    }

    @Transactional
    @CacheEvict(value = "collections", allEntries = true)
    public CollectionResponse toggleCollection(Long id) {
        Collection collection = findById(id);
        collection.setIsActive(!collection.getIsActive());

        Collection updated = collectionRepository.save(collection);
        return collectionMapper.toResponse(updated);
    }

    @Transactional
    @CacheEvict(value = "collections", allEntries = true)
    public CollectionResponse addProductToCollection(Long collectionId, Long productId) {
        Collection collection = findById(collectionId);
        Product product = findProductById(productId);

        if (collection.getProducts().contains(product)) {
            throw new BusinessException("Product is already in this collection");
        }
        collection.getProducts().add(product);
        product.getCollections().add(collection);

        collectionRepository.save(collection);
        productRepository.save(product);
        return collectionMapper.toResponse(collection);
    }

    @Transactional
    @CacheEvict(value = "collections", allEntries = true)
    public CollectionResponse removeProductFromCollection(Long collectionId, Long productId) {
        Collection collection = findById(collectionId);
        Product product = findProductById(productId);

        if (!collection.getProducts().contains(product)) {
            throw new BusinessException("Product is not in this collection");
        }
        collection.getProducts().remove(product);
        product.getCollections().remove(collection);

        collectionRepository.save(collection);
        productRepository.save(product);
        return collectionMapper.toResponse(collection);
    }

    private Collection findById(Long id) {
        return collectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Collection with id " + id + " not found"));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));
    }

}
