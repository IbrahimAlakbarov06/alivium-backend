package alivium.model.dto.request;

import alivium.model.enums.CollectionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionRequest {

    @NotBlank(message = "Collection name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Collection type is required")
    private CollectionType type;

    private String bannerUrl;

    private Boolean active =true;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer displayOrder =0;
}