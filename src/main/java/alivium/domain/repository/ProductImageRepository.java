package alivium.domain.repository;

import alivium.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);

    @Modifying(clearAutomatically = true,flushAutomatically = true)
    @Query("UPDATE ProductImage p SET p.isPrimary = false WHERE p.product.id = :productId")
    void clearPrimaryImages(Long productId);
}
