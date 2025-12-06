package alivium.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "collections")
@Builder
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

//    Collection növü (TRENDING, NEW_ARRIVALS,SEASONAL, EDITOR_PICKS)->olsun bu?
//    @Enumerated(EnumType.STRING)
//    private CollectionType type;

    @ManyToMany(mappedBy = "collections")
    private Set<Product> products = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
