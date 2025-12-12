package alivium.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageRequest {
    @NotNull(message = "File must not be null")
    private MultipartFile file;

    private Boolean isPrimary = false;

    @NotNull(message = "Product ID must not be null")
    private Long productId;
}
