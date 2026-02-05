package alivium.model.dto.request;

import alivium.model.enums.PaymentMethod;
import alivium.model.enums.ShippingMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderRequest {

    private Long addressId;

    @Valid
    private AddressDetails address;

    @NotNull(message = "Shipping method is required")
    private ShippingMethod shippingMethod;

    @Size(max = 50, message = "Voucher code too long")
    private String voucherCode;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Valid
    private CardPaymentDetails cardDetails;

    @Size(max = 500, message = "Notes too long")
    private String notes;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AddressDetails {

        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
        private String fullName;

        @NotBlank(message = "Street address is required")
        @Size(min = 5, max = 500, message = "Street address must be between 5 and 500 characters")
        private String street;

        @NotBlank(message = "City is required")
        @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
        private String city;

        @NotBlank(message = "Country is required")
        @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
        private String country;

        @Size(max = 20, message = "Zip code too long")
        private String zipCode;

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
        private String phoneNumber;

        private Boolean saveForFuture = false;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CardPaymentDetails {
        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "^[0-9]{13,19}$", message = "Invalid card number")
        private String cardNumber;

        @NotBlank(message = "Card holder name is required")
        @Size(min = 2, max = 255, message = "Card holder name must be between 2 and 255 characters")
        private String cardHolderName;

        @NotBlank(message = "Expiry month is required")
        @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Invalid expiry month (01-12)")
        private String expiryMonth;

        @NotBlank(message = "Expiry year is required")
        @Pattern(regexp = "^20[2-9][0-9]$", message = "Invalid expiry year")
        private String expiryYear;

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "Invalid CVV (3-4 digits)")
        private String cvv;

        private Boolean saveCard = false;
    }
}
