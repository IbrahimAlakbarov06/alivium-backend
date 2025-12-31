package alivium.model.dto.request;

import alivium.model.enums.NotificationTemplate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomNotificationRequest {
    @NotNull(message = "Template is required")
    private NotificationTemplate template;

    private Map<String, String> params;

    private String imageUrl;
}
