package alivium.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private String imageUrl;

    private Long parentId;

    private Boolean active = true;
}