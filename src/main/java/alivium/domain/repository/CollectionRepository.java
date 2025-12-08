package alivium.domain.repository;

import alivium.domain.entity.Collection;
import alivium.model.enums.CollectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    Optional<Collection> findByName(String name);

    boolean existsByName(String name);

    List<Collection> findByIsActiveTrueOrderByDisplayOrderAsc();

    @Query("SELECT c FROM Collection c WHERE c.isActive = true AND c.type = :type ORDER BY c.displayOrder ASC")
    List<Collection> findActiveByTypeOrderByDisplayOrder(CollectionType type);

    @Query("SELECT c FROM Collection c WHERE c.isActive = true AND " +
            "(c.startDate IS NULL OR c.startDate <= :now) AND" +
            "(c.endDate IS NULL OR c.endDate >= :now) " +
            "ORDER BY c.displayOrder ASC")
    List<Collection> findActiveCollectionsInDateRange(LocalDateTime now);

}