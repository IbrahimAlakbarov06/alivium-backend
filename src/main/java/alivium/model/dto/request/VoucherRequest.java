package alivium.model.dto.request;

import alivium.model.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherRequest {

    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code can be max 50 characters")
    private String code;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title can be max 100 characters")
    private String title;

    private String description;

    @NotNull(message = "Discount type is required")
    private DiscountType type;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.0", message = "Min order amount cannot be negative")
    private BigDecimal minOrderAmount;

    @DecimalMin(value = "0.0", message = "Max discount amount cannot be negative")
    private BigDecimal maxDiscountAmount;

    @Positive(message = "Total usage limit must be positive")
    private Integer totalUsageLimit;

    @NotNull(message = "Per user limit is required")
    @Positive(message = "Per user limit must be positive")
    private Integer perUserLimit;

    @Future(message = "Expiry date must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiryDate;
}
