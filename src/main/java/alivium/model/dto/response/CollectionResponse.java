package alivium.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CollectionType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionResponse {
    private Long id;
    private String name;
    private String description;
    private CollectionType type;
    private LocalDateTime createdAt;
}
