package alivium.domain.repository;

import alivium.domain.entity.Notification;
import alivium.model.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status);

    Long countByUserIdAndStatus(Long userId, NotificationStatus status);

    void deleteByUserId(Long userId);
}