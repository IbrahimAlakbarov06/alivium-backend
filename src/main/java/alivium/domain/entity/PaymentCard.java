package alivium.domain.entity;

import alivium.model.enums.CardType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment-cards")
@Builder
public class PaymentCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "card_holder_name", nullable = false, length = 100)
    private String cardHolderName;

    @Column(name = "card_token", nullable = false)
    private String cardToken;

    @Column(name = "last_4_digits", nullable = false, length = 4)
    private String last4Digits;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;

    @Column(name = "expiration_month", nullable = false)
    private Integer expirationMonth;

    @Column(name = "expiration_year", nullable = false)
    private Integer expirationYear;

    @Column(name = "is_default", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDefault = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
