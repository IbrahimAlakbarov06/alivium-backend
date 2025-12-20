package alivium.domain.repository;

import alivium.domain.entity.Category;
import alivium.domain.entity.Collection;
import alivium.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);

    List<Product> findAllByActiveTrue();

    List<Product> findByCategoriesContainingAndActiveTrue(Category category);

    List<Product> findByCollectionsContainingAndActiveTrue(Collection collection);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.categories c " +
            "WHERE p.active = true AND " +
            "(:query IS NULL OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchProducts(@Param("query") String query);


    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.categories cat " +
            "LEFT JOIN p.variants v " +
            "LEFT JOIN p.collections col " +
            "WHERE p.active = true " +
            "AND (:query IS NULL OR " +
            "    LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "    LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:categoryIds IS NULL OR cat.id IN :categoryIds) " +
            "AND (:collectionIds IS NULL OR col.id IN :collectionIds) " +
            "AND (:minRating IS NULL OR p.averageRating >= :minRating) " +
            "AND (:colors IS NULL OR v.color IN :colors) " +
            "AND (:sizes IS NULL OR v.size IN :sizes) " +
            "AND (:inStock IS NULL OR :inStock = false OR v.stockQuantity > 0)")
    List<Product> searchWithFilters(
            @Param("query") String query,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("collectionIds") List<Long> collectionIds,
            @Param("minRating") Double minRating,
            @Param("colors") List<String> colors,
            @Param("sizes") List<String> sizes,
            @Param("inStock") Boolean inStock
    );
}
