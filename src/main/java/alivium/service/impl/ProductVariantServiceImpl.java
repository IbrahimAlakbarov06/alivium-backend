package alivium.service.impl;

import alivium.domain.entity.Product;
import alivium.domain.entity.ProductVariant;
import alivium.domain.entity.Wishlist;
import alivium.domain.repository.ProductRepository;
import alivium.domain.repository.ProductVariantRepository;
import alivium.domain.repository.WishlistRepository;
import alivium.exception.AlreadyExistsException;
import alivium.exception.BusinessException;
import alivium.exception.NotFoundException;
import alivium.mapper.ProductVariantMapper;
import alivium.model.dto.request.ProductVariantRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ProductVariantResponse;
import alivium.model.enums.NotificationTemplate;
import alivium.service.NotificationTemplateService;
import alivium.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantMapper variantMapper;
    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final NotificationTemplateService notificationTemplateService;

    @Transactional
    @CacheEvict(value = "variants", allEntries = true)
    public ProductVariantResponse addVariantToProduct(Long productId, ProductVariantRequest request) {
        Product product = findProductById(productId);

        if (variantRepository.existsBySku(request.getSku())) {
            throw new AlreadyExistsException("Product variant already exists with sku: " + request.getSku());
        }

        if (variantRepository.findByProductIdAndColorAndSize(productId, request.getColor(), request.getSize()).isPresent()) {
            throw new AlreadyExistsException("Product variant already exists with sku: " + request.getSku());
        }

        ProductVariant variant = variantMapper.toEntity(request);
        variant.setProduct(product);

        ProductVariant saved =variantRepository.save(variant);
        return variantMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "variants", key = "'product-' + #productId")
    public List<ProductVariantResponse> getVariantsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found with id: " + productId);
        }

        List<ProductVariant> productVariants = variantRepository.findByProductId(productId);
        return variantMapper.toListResponse(productVariants);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "variants", key = "'available-' + #productId")
    public List<ProductVariantResponse> getAvailableVariantsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found with id: " + productId);
        }

        List<ProductVariant> variants = variantRepository.findByProductIdAndAvailableTrue(productId);
        return variantMapper.toListResponse(variants);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "variants", key = "#variantId")
    public ProductVariantResponse getVariantById(Long variantId) {
        ProductVariant variant = findVariantById(variantId);
        return variantMapper.toResponse(variant);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "variants", key = "'sku-' + #sku")
    public ProductVariantResponse getVariantBySku(String sku) {
        ProductVariant variant = variantRepository.findBySku(sku)
                .orElseThrow(() -> new NotFoundException("Variant not found with SKU: " + sku));
        return variantMapper.toResponse(variant);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "variants", key = "#productId + '-color-' + #color")
    public List<ProductVariantResponse> getVariantsByColor(Long productId,String color) {
        if (!productRepository.existsById(productId)){
            throw new NotFoundException("Product not found with id: " + productId);
        }

        List<ProductVariant> variants = variantRepository.findByProductIdAndColor(productId, color);
        return variantMapper.toListResponse(variants);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "variants", key = "#productId + '-size-' + #size")
    public List<ProductVariantResponse> getVariantsBySize(Long productId,String size) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found with id: " + productId);
        }

        List<ProductVariant> variants =variantRepository.findByProductIdAndSize(productId, size);
        return variantMapper.toListResponse(variants);
    }

    @Transactional
    @CacheEvict(value = "variants", allEntries = true)
    public ProductVariantResponse updateVariant(Long variantId,ProductVariantRequest request) {
        ProductVariant variant = findVariantById(variantId);

        if (request.getSku() != null && !request.getSku().equals(variant.getSku())) {
            if (variantRepository.existsBySku(request.getSku())) {
                throw new AlreadyExistsException("Product variant already exists with sku: " + request.getSku());
            }
        }

        variantMapper.updateVariantFromRequest(variant,request);
        ProductVariant updatedVariant = variantRepository.save(variant);
        return variantMapper.toResponse(updatedVariant);
    }

    @Transactional
    @CacheEvict(value = "variants", allEntries = true)
    public MessageResponse deleteVariant(Long variantId) {
        ProductVariant variant = findVariantById(variantId);

        variantRepository.delete(variant);
        return new MessageResponse("Variant deleted successfully");
    }

    @Transactional
    @CacheEvict(value = "variants", allEntries = true)
    public ProductVariantResponse updateStock(Long variantId,Integer quantity) {
        ProductVariant variant = findVariantById(variantId);

        if (quantity < 0) {
            throw new BusinessException("Stock quantity cannot be negative");
        }

        variant.setStockQuantity(quantity);
        variant.setAvailable(quantity>0);

        ProductVariant updatedVariant = variantRepository.save(variant);
        return variantMapper.toResponse(updatedVariant);
    }

    @Transactional
    @CacheEvict(value = "variants", allEntries = true)
    public ProductVariantResponse increaseStock(Long variantId,Integer quantity) {
        ProductVariant variant = findVariantById(variantId);

        if (quantity <= 0) {
            throw new BusinessException("Quantity must be positive");
        }

        int previousStock = variant.getStockQuantity();

        variant.setStockQuantity(variant.getStockQuantity()+quantity);
        variant.setAvailable(true);

        ProductVariant updatedVariant = variantRepository.save(variant);

        if (previousStock==0 && variant.getStockQuantity()>0){
            Product product =variant.getProduct();
            List<Wishlist> wishlists= wishlistRepository.findByProductId(product.getId());

            for (Wishlist wishlist : wishlists) {
                Map<String, String> params = new HashMap<>();
                params.put("productName", product.getName());
                params.put("price", product.getPrice().toString());

                notificationTemplateService.sendNotification(wishlist.getUser(), NotificationTemplate.WISHLIST_ITEM_BACK_IN_STOCK, params);
            }
        }

        return variantMapper.toResponse(updatedVariant);
    }

    @Transactional
    @CacheEvict(value = "variants", allEntries = true)
    public ProductVariantResponse decreaseStock(Long variantId, Integer quantity) {
        ProductVariant variant = findVariantById(variantId);

        if (quantity <= 0) {
            throw new BusinessException("Quantity must be positive");
        }

        int newStock = variant.getStockQuantity()-quantity;
        if (newStock < 0) {
            throw new BusinessException("Insufficient stock. Available: " + variant.getStockQuantity());
        }

        variant.setStockQuantity(newStock);
        variant.setAvailable(newStock>0);

        ProductVariant updatedVariant = variantRepository.save(variant);
        return variantMapper.toResponse(updatedVariant);
    }

    @Transactional
    @CacheEvict(value = "variants", allEntries = true)
    public ProductVariantResponse toggleAvailability(Long variantId) {
        ProductVariant variant = findVariantById(variantId);
        variant.setAvailable(!variant.getAvailable());

        ProductVariant updated = variantRepository.save(variant);
        return variantMapper.toResponse(updated);
    }


    private ProductVariant findVariantById(Long variantId) {
        return variantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Variant not found with id: " + variantId));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
    }


}
