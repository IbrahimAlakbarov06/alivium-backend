package alivium.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageMinimalResponse {
    private Long id;
    private String imageKey;
    private String imageUrl;
}
