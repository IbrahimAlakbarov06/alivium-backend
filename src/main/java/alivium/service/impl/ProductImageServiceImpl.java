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
import alivium.service.MinioService;
import alivium.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository pimRepo;
    private final MinioService minioService;
    private final MinioProperties minioProperties;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    @Override
    @Transactional
    @CacheEvict(value = "productImages", key = "#request.productId")
    public ProductImageResponse uploadProductImage(ProductImageRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + request.getProductId()));

        String key = minioService.uploadFile(request.getFile(), productBucket());
        String url = minioService.getPreSignedUrl(productBucket(), key, 3600);

        ProductImage image = productImageMapper.toEntity(request, product);
        image.setImageKey(key);
        image.setImageUrl(url);
        ProductImage saved = pimRepo.save(image);
        return productImageMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "productImages", allEntries = true)
    public void deleteProductImage(Long imageId) {
        ProductImage productImage = findById(imageId);
        deleteFileFromStorage(productImage);
        pimRepo.delete(productImage);
    }

    @Override
    public String getImageDownloadUrl(Long imageId) {
        ProductImage image = findById(imageId);
        return minioService.getPreSignedUrl(productBucket(), image.getImageKey(), 3600);
    }

    @Override
    @Cacheable(value = "productImages", key = "#productId")
    public List<ProductImageResponse> getProductImagesByProductId(Long productId) {
        List<ProductImage> productImageList = pimRepo.findByProductId(productId);
        return productImageList.stream().map(productImageMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "productImages", allEntries = true)
    public ProductImageResponse setPrimaryImage(Long imageId) {
        ProductImage productImage = findById(imageId);
        Long productId=productImage.getProduct().getId();

        pimRepo.clearPrimaryImages(productId);
        productImage.setIsPrimary(true);
        ProductImage saved = pimRepo.save(productImage);

        return productImageMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InputStream downloadProductImage(Long imageId) {
        ProductImage image = findById(imageId);
        return minioService.downloadFile(productBucket(), image.getImageKey());
    }

    @Override
    @Transactional
    @CacheEvict(value = "productImages", key = "#imageId")
    public ProductImageResponse updateProductImage(Long imageId, MultipartFile newFile) {
        ProductImage existing = findById(imageId);

        deleteFileFromStorage(existing);
        String key = minioService.uploadFile(newFile, productBucket());
        String url = minioService.getPreSignedUrl(productBucket(), key, 3600);

        existing.setImageKey(key);
        existing.setImageUrl(url);

        ProductImage saved = pimRepo.save(existing);
        return productImageMapper.toResponse(saved);
    }

    @Override
    public ProductImage findById(Long imageId) {
        return pimRepo.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Product Image Not Found with id: " + imageId));
    }

    private void deleteFileFromStorage(ProductImage image) {
        minioService.deleteFile(productBucket(), image.getImageKey());
    }

    private String productBucket() {
        return minioProperties.getBucket().getProduct();
    }
}
