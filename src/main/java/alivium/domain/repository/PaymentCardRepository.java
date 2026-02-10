package alivium.domain.repository;

import alivium.domain.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {
    List<PaymentCard> findByUserId(Long userId);
    Optional<PaymentCard> findByUserIdAndIsDefaultTrue(Long userId);

    @Modifying(clearAutomatically = true,flushAutomatically = true)
    @Query("""
         update PaymentCard pc
               set pc.isDefault=false
                     where pc.user.id=:userId and pc.isDefault=true
      """)
    void clearDefaultCard(Long userId);
}
