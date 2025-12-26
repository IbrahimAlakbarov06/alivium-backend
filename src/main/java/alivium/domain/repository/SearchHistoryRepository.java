package alivium.domain.repository;

import alivium.domain.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findByUserIdOrderBySearchedAtDesc(Long userId);

    List<SearchHistory> findTop10ByUserIdOrderBySearchedAtDesc(Long userId);

    void deleteByUserId(Long userId);
}