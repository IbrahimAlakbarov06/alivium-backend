package alivium.service;

import alivium.domain.entity.ProductImage;
import alivium.model.dto.request.ProductImageRequest;
import alivium.model.dto.response.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ProductImageService {
    ProductImageResponse uploadProductImage(ProductImageRequest request);
    void deleteProductImage(Long imageId);
    String getImageDownloadUrl(Long imageId);
    List<ProductImageResponse> getProductImagesByProductId(Long productId);
    ProductImageResponse setPrimaryImage(Long imageId);
    InputStream downloadProductImage(Long imageId);
    ProductImageResponse updateProductImage(Long imageId, MultipartFile newFile);
    ProductImage findById(Long imageId);
}
