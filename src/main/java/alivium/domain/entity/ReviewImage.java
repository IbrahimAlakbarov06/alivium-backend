package alivium.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "review_images")
public class ReviewImage extends BaseImage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

//    @Column(nullable = false, length = 1000)
//    private String imageUrl;
//
//    @Column(nullable = false)
//    private String imageKey;
//
//    private LocalDateTime imageUrlExpiry;
//
//    @CreationTimestamp
//    @Column(nullable = false, updatable = false)
//    private LocalDateTime createdAt;
}