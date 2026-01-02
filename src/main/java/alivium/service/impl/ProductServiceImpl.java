package alivium.service.impl;

import alivium.domain.entity.*;
import alivium.domain.repository.CategoryRepository;
import alivium.domain.repository.CollectionRepository;
import alivium.domain.repository.ProductRepository;
import alivium.domain.repository.WishlistRepository;
import alivium.exception.AlreadyExistsException;
import alivium.exception.NotFoundException;
import alivium.mapper.ProductMapper;
import alivium.mapper.ProductVariantMapper;
import alivium.model.dto.request.ProductCreateRequest;
import alivium.model.dto.request.ProductUpdateRequest;
import alivium.model.dto.response.ProductResponse;
import alivium.model.enums.NotificationTemplate;
import alivium.service.NotificationTemplateService;
import alivium.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final CollectionRepository collectionRepository;
    private final NotificationTemplateService notificationTemplateService;
    private final WishlistRepository wishlistRepository;

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(ProductCreateRequest request) {
        if(productRepository.existsByName(request.getName())){
            throw new AlreadyExistsException("Product already exists with name: "+request.getName());
        }
        if (request.getDiscountPrice() != null && request.getDiscountPrice().compareTo(request.getPrice()) > 0) {
            throw new IllegalArgumentException("Discount price cannot be greater than the original price");
        }

        Product product=productMapper.toEntity(request);
        productMapper.setProductRelationsFromRequest(request,product);

        Product savedProduct=productRepository.save(product);

        Map<String, String> params = new HashMap<>();
        params.put("productName", product.getName());
        params.put("price", product.getPrice().toString());

        notificationTemplateService.sendNotificationToAll(NotificationTemplate.NEW_ARRIVAL, params);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product=findById(productId);

        if(request.getDiscountPrice()!=null && request.getDiscountPrice().compareTo(product.getPrice())>0){
            throw new IllegalArgumentException("Discount price cannot be greater than the original price");
        }
        productMapper.updateEntityFromDto(request,product);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public void deleteProduct(Long productId) {
        Product product = findById(productId);

        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'all'")
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#productId")
    public ProductResponse getProductById(Long productId) {
        Product product =findById(productId);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'active'")
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findAllByActiveTrue().stream()
                .map(productMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'category:' + #categoryId")
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));

        List<Product> products = productRepository.findByCategoriesContainingAndActiveTrue(category);
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'collection:' + #collectionId")
    public List<ProductResponse> getProductsByCollectionId(Long collectionId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new NotFoundException("Collection not found with id: " + collectionId));

        List<Product> products = productRepository.findByCollectionsContainingAndActiveTrue(collection);
        return products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductResponse updateProductDiscountPrice(Long productId, BigDecimal discountPrice) {
        Product product = findById(productId);

        if (discountPrice != null && discountPrice.compareTo(product.getPrice()) > 0) {
            throw new IllegalArgumentException("Discount price cannot be greater than the original price");
        }

        product.setDiscountPrice(discountPrice);
        Product updatedProduct = productRepository.save(product);

        if (discountPrice != null) {
            sendWishlistPriceDropNotification(product, discountPrice);
        }

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductResponse switchProductStatus(Long productId) {
        Product product = findById(productId);
        product.setActive(!product.getActive());

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    private void sendWishlistPriceDropNotification(Product product, BigDecimal newPrice) {
        List<Wishlist> wishlists = wishlistRepository.findByProductId(product.getId());

        if (wishlists.isEmpty()){
            return;
        }

        BigDecimal oldPrice = product.getPrice();
        double discountPercent = oldPrice.subtract(newPrice)
                .divide(oldPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        for (Wishlist wishlist : wishlists) {
            Map<String, String> params = new HashMap<>();
            params.put("productName", product.getName());
            params.put("price", newPrice.toString());
            params.put("discount", String.format("%.0f", discountPercent));

            notificationTemplateService.sendNotification(
                    wishlist.getUser(),
                    NotificationTemplate.WISHLIST_ITEM_ON_SALE,
                    params
            );
        }
    }

    private Product findById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

}
