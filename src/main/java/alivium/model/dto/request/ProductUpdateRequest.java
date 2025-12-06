package alivium.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Boolean active;
}
