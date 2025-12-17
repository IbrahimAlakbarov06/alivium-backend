package alivium.domain.repository;

import alivium.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    @Query("select c from Cart c left join fetch  c.cartItems ci left join fetch ci.product where c.user.id=:userId")
    Optional<Cart> findByUserIdWithItems(Long userId);

    boolean existsByUserId(Long userId);
}
