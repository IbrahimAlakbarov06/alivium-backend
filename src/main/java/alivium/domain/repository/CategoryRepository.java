package alivium.domain.repository;

import alivium.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    List<Category> findByActiveTrue();

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true")
    List<Category> findActiveMainCategories();

    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.active = true")
    List<Category> findActiveSubCategoriesByParentId(Long parentId);
}