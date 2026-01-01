package alivium.domain.repository;

import alivium.domain.entity.Feedback;
import alivium.model.enums.FeedbackStatus;
import alivium.model.enums.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    List<Feedback> findByUserId(Long userId);

    @Query("select avg(f.rating) from Feedback f")
    Double calculateAverageRating();

    List<Feedback> findByStatus(FeedbackStatus status);

    List<Feedback> findByType(FeedbackType type);
}
