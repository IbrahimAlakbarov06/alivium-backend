package alivium.domain.entity;

import alivium.model.enums.CollectionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "collections")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollectionType type;

    @Column(length = 500)
    private String bannerImageUrl;

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @ManyToMany(mappedBy = "collections", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Product> products = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}