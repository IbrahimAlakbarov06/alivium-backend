package alivium.model.dto.response;

import alivium.model.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String country;
    private String city;
    private String district;
    private String street;
    private String zipCode;
    private Boolean isDefault;
    private AddressType addressType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
