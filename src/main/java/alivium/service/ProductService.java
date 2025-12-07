package alivium.service;

import alivium.model.dto.request.ProductCreateRequest;
import alivium.model.dto.request.ProductUpdateRequest;
import alivium.model.dto.response.ProductResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductCreateRequest request);
    ProductResponse updateProduct(Long productId, ProductUpdateRequest request);
    void deleteProduct(Long productId);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long productId);
    List<ProductResponse> getActiveProducts();
    List<ProductResponse> getProductsByCategoryId(Long categoryId);
    List<ProductResponse> getProductsByCollectionId(Long collectionId);
    ProductResponse updateProductDiscountPrice(Long productId, BigDecimal discountPrice);
    ProductResponse switchProductStatus(Long productId);
}
