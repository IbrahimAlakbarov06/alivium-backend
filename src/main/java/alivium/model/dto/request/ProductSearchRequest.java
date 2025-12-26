package alivium.model.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSearchRequest {

    private String query;

    private List<Long> categoryIds;

    private List<Long> collectionIds;

    @Min(value = 0, message = "Min price cannot be negative")
    private BigDecimal minPrice;

    @Min(value = 0, message = "Max price cannot be negative")
    private BigDecimal maxPrice;

    private List<String> colors;

    private List<String> sizes;

    @Min(value = 0, message = "Min rating must be between 0 and 5")
    private Double minRating;

    private Boolean inStock;

    private String sortBy;

    private String sortOrder;
}