package alivium.domain.repository;

import alivium.domain.entity.Category;
import alivium.domain.entity.Collection;
import alivium.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);

    List<Product> findAllByActiveTrue();

    List<Product> findByCategoriesContainingAndActiveTrue(Category category);

    List<Product> findByCollectionsContainingAndActiveTrue(Collection collection);
}
