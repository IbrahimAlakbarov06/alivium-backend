package alivium.domain.repository;

import alivium.domain.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndProductIdAndVariantId(Long cartId, Long productId, Long variantId);

    Optional<CartItem> findByIdAndCartId(Long itemId, Long cartId);

    @Query("select count (ci) FROM CartItem ci where ci.cart.id=:cartId")
    Long countByCartId(Long cartId);

    void deleteByCartId(Long cartId);
}
