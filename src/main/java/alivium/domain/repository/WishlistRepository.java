package alivium.domain.repository;

import alivium.domain.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    List<Wishlist> findByUserIdOrderByAddedAtDesc(Long userId);

    Optional<Wishlist> findByProductIdAndUserId(Long productId, Long userId);

    @Query("select count(w) from Wishlist w where w.user.id=:userId")
    Long countByUserId(Long userId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

}
