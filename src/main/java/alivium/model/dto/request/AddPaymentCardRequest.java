package alivium.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddPaymentCardRequest {
    @NotBlank(message = "Card number is required")
    @Pattern(
            regexp = "^[0-9]{13,19}$",
            message = "Card number must be 13-19 digits"
    )
    private String cardNumber;

    @NotNull(message = "Expiry month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer expiryMonth;

    @NotNull(message = "Expiry year is required")
    @Min(value = 2025, message = "Year cannot be in the past")
    @Max(value = 2099, message = "Invalid year")
    private Integer expiryYear;

    @NotBlank(message = "CVV is required")
    @Pattern(
            regexp = "^[0-9]{3,4}$",
            message = "CVV must be 3 or 4 digits"
    )
    private String cvv;

    private boolean isDefault;
}
