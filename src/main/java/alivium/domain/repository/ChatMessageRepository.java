package alivium.domain.repository;

import alivium.domain.entity.ChatMessage;
import alivium.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);
    List<ChatMessage> findByChatRoomAndReadFalse(ChatRoom chatRoom);
}
