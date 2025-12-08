package alivium.domain.repository;

import alivium.domain.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

//    List<PaymentCard> findByUserId(Long userId);
//
//    Optional<PaymentCard> findByIdAndUserId(Long id, Long userId);
//
//    List<PaymentCard> findByUserIdAndBrand(Long userId, String brand);
//
//    Optional<PaymentCard> findByUserIdAndIsDefaultTrue(Long userId);
//
//    boolean existsByUserIdAndLast4Digits(Long userId, String last4Digits);
//
//    int countByUserId(Long userId);
}
