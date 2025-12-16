package alivium.service;

import alivium.model.dto.request.ProductVariantRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ProductVariantResponse;

import java.util.List;

public interface ProductVariantService {

    ProductVariantResponse addVariantToProduct(Long productId, ProductVariantRequest request);

    List<ProductVariantResponse> getVariantsByProduct(Long productId);

    List<ProductVariantResponse> getAvailableVariantsByProduct(Long productId);

    ProductVariantResponse getVariantById(Long variantId);

    ProductVariantResponse getVariantBySku(String sku);

    List<ProductVariantResponse> getVariantsByColor(Long productId, String color);

    List<ProductVariantResponse> getVariantsBySize(Long productId, String size);

    ProductVariantResponse updateVariant(Long variantId, ProductVariantRequest request);

    MessageResponse deleteVariant(Long variantId);

    ProductVariantResponse updateStock(Long variantId, Integer quantity);

    ProductVariantResponse increaseStock(Long variantId, Integer quantity);

    ProductVariantResponse decreaseStock(Long variantId, Integer quantity);

    ProductVariantResponse toggleAvailability(Long variantId);
}
