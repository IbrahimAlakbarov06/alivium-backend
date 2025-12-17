package alivium.service.impl;

import alivium.config.MinioProperties;
import alivium.domain.entity.Product;
import alivium.domain.entity.ProductImage;
import alivium.domain.repository.ProductImageRepository;
import alivium.domain.repository.ProductRepository;
import alivium.exception.NotFoundException;
import alivium.mapper.ProductImageMapper;
import alivium.model.dto.request.ProductImageRequest;
import alivium.model.dto.response.ProductImageResponse;
import alivium.service.ImageStorageService;
import alivium.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository pimRepo;
    private final ImageStorageService imageStorageService;
    private final MinioProperties minioProperties;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    @Transactional
    public String getValidImageUrl(ProductImage productImage){
        if(productImage.getImageUrl()==null
                || productImage.getImageUrlExpiry()==null
                || productImage.getImageUrlExpiry().isBefore(LocalDateTime.now())){
            String newUrl= imageStorageService.getPreSignedUrl(productBucket(),productImage.getImageKey(),3600);
            productImage.setImageUrl(newUrl);
            LocalDateTime newExpiry=LocalDateTime.now().plusSeconds(3600);
            productImage.setImageUrlExpiry(newExpiry);
            updateImageUrlAsync(productImage.getId(), newUrl, newExpiry);
        }
        return productImage.getImageUrl();
    }

    @Async
    @Transactional
    public void updateImageUrlAsync(Long imageId, String url, LocalDateTime expiry) {
        ProductImage image = findById(imageId);

        image.setImageUrl(url);
        image.setImageUrlExpiry(expiry);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"productImages", "products", "wishlist"}, allEntries = true)
    public ProductImageResponse uploadProductImage(ProductImageRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + request.getProductId()));

        String key = imageStorageService.uploadFile(request.getFile(), productBucket());
        String url = imageStorageService.getPreSignedUrl(productBucket(), key, 3600);

        ProductImage image = productImageMapper.toEntity(request, product);
        image.setImageKey(key);
        image.setImageUrl(url);
        image.setImageUrlExpiry(LocalDateTime.now().plusSeconds(3600));
        ProductImage saved = pimRepo.save(image);

        return productImageMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"productImages", "products", "wishlist"}, allEntries = true)
    public void deleteProductImage(Long imageId) {
        ProductImage productImage = findById(imageId);
        deleteFileFromStorage(productImage);
        pimRepo.delete(productImage);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"productImages", "products", "wishlist"}, allEntries = true)
    public void deleteProductImages(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));

        List<ProductImage> images = pimRepo.findByProductId(productId);

        for (ProductImage image : images) {
            imageStorageService.deleteFile(productBucket(), image.getImageKey());
            pimRepo.delete(image);
        }
    }

    @Override
    @Transactional
    public String getImageDownloadUrl(Long imageId) {
        ProductImage image = findById(imageId);
        return getValidImageUrl(image);
    }

    @Override
    @Transactional
    @Cacheable(value = "productImages", key = "#productId")
    public List<ProductImageResponse> getProductImagesByProductId(Long productId) {
       productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " +productId));
        List<ProductImage> productImageList = pimRepo.findByProductId(productId);
        return productImageList.stream()
                .map(img -> {
                    img.setImageUrl(getValidImageUrl(img));
                    return productImageMapper.toResponse(img);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "productImages", allEntries = true)
    public ProductImageResponse setPrimaryImage(Long imageId) {
        ProductImage productImage = findById(imageId);
        Long productId=productImage.getProduct().getId();

        pimRepo.clearPrimaryImages(productId);

        productImage.setIsPrimary(true);
        productImage.setImageUrl(getValidImageUrl(productImage));
        ProductImage saved = pimRepo.save(productImage);

        return productImageMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InputStream downloadProductImage(Long imageId) {
        ProductImage image = findById(imageId);
        return imageStorageService.downloadFile(productBucket(), image.getImageKey());
    }

    @Override
    @Transactional
    @CacheEvict(value = "productImages",allEntries = true)
    public ProductImageResponse updateProductImage(Long imageId, MultipartFile newFile) {
        ProductImage existing = findById(imageId);

        deleteFileFromStorage(existing);
        String key = imageStorageService.uploadFile(newFile, productBucket());

        existing.setImageKey(key);
        existing.setImageUrl(null);
        existing.setImageUrlExpiry(null);

        existing.setImageUrl(imageStorageService.getPreSignedUrl(productBucket(), key, 3600));
        ProductImage saved = pimRepo.save(existing);

        return productImageMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "reviewImages", allEntries = true)
    public ProductImageResponse refreshUrl(Long imageId) {
        ProductImage image = findById(imageId);
        String url = imageStorageService.getPreSignedUrl(productBucket(), image.getImageKey(), 3600);
        image.setImageUrl(url);
        image.setImageUrlExpiry(LocalDateTime.now().plusSeconds(3600));
        ProductImage saved = pimRepo.save(image);
        return productImageMapper.toResponse(saved);
    }

    @Override
    public ProductImage findById(Long imageId) {
        return pimRepo.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Product Image Not Found with id: " + imageId));
    }

    private void deleteFileFromStorage(ProductImage image) {
        imageStorageService.deleteFile(productBucket(), image.getImageKey());
    }

    private String productBucket() {
        return minioProperties.getBucket().getProduct();
    }
}
