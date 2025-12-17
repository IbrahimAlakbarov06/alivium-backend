package alivium.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseImage {
    @Column(columnDefinition = "TEXT")
    protected String imageUrl;

    private String imageKey;

    private LocalDateTime imageUrlExpiry;

    @CreationTimestamp
//    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
