package alivium.domain.entity;

import alivium.model.enums.ShippingMethod;
import alivium.model.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shippings")
@Builder
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingMethod shippingMethod;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(unique = true)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingStatus shippingStatus;

    @Column(columnDefinition = "TEXT")
    private String shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User courier;

    private LocalDateTime courierAssignedAt;

    private LocalDateTime courierAcceptedAt;

    @Column(columnDefinition = "TEXT")
    private String courierNotes;

    private LocalDateTime estimatedDeliveryDate;

    private LocalDateTime actualDeliveryDate;

    private LocalDateTime shippedDate;

    private LocalDateTime pickedUpAt;

    private LocalDateTime outForDeliveryAt;

    @Column(length = 500)
    private String recipientName;

    @Column(columnDefinition = "TEXT")
    private String deliveryNotes;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    private Integer deliveryAttempts = 0;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
