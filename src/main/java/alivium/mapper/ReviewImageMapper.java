package alivium.mapper;

import alivium.domain.entity.ReviewImage;
import alivium.model.dto.response.ReviewImageResponse;
import org.springframework.stereotype.Component;

@Component
public class ReviewImageMapper {
    public ReviewImageResponse toResponse(ReviewImage image) {
        if (image == null) return null;

        return ReviewImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .imageKey(image.getImageKey())
                .createdAt(image.getCreatedAt())
                .build();
    }
}
