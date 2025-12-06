package alivium.domain.repository;

import alivium.domain.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductId(Long productId);

    Optional<ProductVariant> findBySku(String sku);

    boolean existsBySku(String sku);

    List<ProductVariant> findByProductIdAndAvailableTrue(Long productId);

    List<ProductVariant> findByProductIdAndColor(Long productId, String color);

    List<ProductVariant> findByProductIdAndSize(Long productId, String size);

    Optional<ProductVariant> findByProductIdAndColorAndSize(Long productId, String color, String size);
}