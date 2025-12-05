package alivium.model.dto.request;

import alivium.model.enums.AddressType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressTypeRequest {
    @NotNull(message = "Address type is required")
    private AddressType addressType;
}
