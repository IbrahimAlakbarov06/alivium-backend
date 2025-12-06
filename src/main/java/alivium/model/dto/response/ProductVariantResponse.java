package alivium.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantResponse {
    private Long id;
    private String color;
    private String size;
    private Integer stockQuantity;
    private String sku;
    private BigDecimal additionalPrice;
    private Boolean available;
    private Long productId;
    private String productName;
}
