package alivium.domain.repository;

import alivium.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Review> findByProductIdAndActiveTrueOrderByCreatedAtDesc(Long productId);

    List<Review> findByProductIdAndUserId(Long productId, Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    List<Review> findByProductIdAndRatingAndActiveTrue(Long productId, Integer rating);

    @Query("select avg(r) from Review r where r.product.id= :productId and r.active=true")
    Double calculateRating(Long productId);

    @Query("select count(r) from Review r where r.product.id =:productId and r.active=true")
    Long countByProductId(Long productId);


}
