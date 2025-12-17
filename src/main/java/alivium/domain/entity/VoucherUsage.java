package alivium.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "voucher_usages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Voucher voucher;

    @ManyToOne(optional = false)
    private User user;

    @OneToOne
    private Order order;

    @CreationTimestamp
    private LocalDateTime usedAt;
}
