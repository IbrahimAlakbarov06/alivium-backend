package alivium.domain.repository;

import alivium.domain.entity.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage,Long> {

    long countByVoucherId(Long voucherId);

    int countByUserIdAndVoucherId(Long userId, Long voucherId);

    @Query("""
        select count(distinct vu.user.id)
        from VoucherUsage vu
        where vu.voucher.id = :voucherId
    """)
    int countUniqueUsersByVoucherId(Long voucherId);


    boolean existsByUserIdAndVoucherIdAndOrderId(
            Long userId,
            Long voucherId,
            Long orderId
    );


    List<VoucherUsage> findByUserId(Long userId);

    List<VoucherUsage> findByVoucherId(Long voucherId);

    List<VoucherUsage> findByUserIdAndVoucherId(Long userId, Long voucherId);

    List<VoucherUsage> findByUserIdAndVoucherIdAndOrderId(
            Long userId,
            Long voucherId,
            Long orderId
    );
}
