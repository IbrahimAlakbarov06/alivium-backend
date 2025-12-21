package alivium.domain.repository;

import alivium.domain.entity.ChatRoom;
import alivium.domain.entity.User;
import alivium.model.enums.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUser(User user);
    List<ChatRoom> findByAdmin(User admin);
    List<ChatRoom> findByStatus(ChatStatus status);
    List<ChatRoom> findByUserAndStatus(User user, ChatStatus status);
    List<ChatRoom> findByAdminAndStatus(User admin, ChatStatus status);
    boolean existsByUserAndStatus(User user, ChatStatus chatStatus);
}
