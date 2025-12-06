package alivium.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequest {
    @NotBlank(message = "Product name cannot be empty")
    @Size(max = 255, message = "Product name must be less than 255 characters")
    private String name;

    @Size(max = 2000, message = "Description must be less than 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount price cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Discount price format is invalid")
    private BigDecimal discountPrice;

    @NotNull(message = "Active status must be provided")
    private Boolean active;

    private Set<@NotNull(message = "Category id cannot be null") Long> categoryIds;
    private Set<@NotNull(message = "Collection id cannot be null") Long> collectionIds;

    private Set<@Valid ProductVariantRequest> variants;
}
