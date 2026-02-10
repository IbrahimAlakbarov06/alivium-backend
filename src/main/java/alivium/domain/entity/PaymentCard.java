package alivium.domain.entity;

import alivium.model.enums.CardType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "payment_cards")
@ToString(exclude = "user")
public class PaymentCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "card_token", nullable = false)
    private String cardToken;

    @Column(name = "last_4_digits", nullable = false, length = 4)
    private String last4Digits;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType=CardType.UNKNOWN;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    private Integer expiryMonth;
    private Integer expiryYear;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
