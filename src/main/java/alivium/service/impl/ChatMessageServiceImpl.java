package alivium.service.impl;

import alivium.domain.entity.ChatMessage;
import alivium.domain.entity.ChatRoom;
import alivium.domain.entity.User;
import alivium.domain.repository.ChatMessageRepository;
import alivium.domain.repository.ChatRoomRepository;
import alivium.domain.repository.UserRepository;
import alivium.exception.BusinessException;
import alivium.exception.NotFoundException;
import alivium.mapper.ChatMessageMapper;
import alivium.model.dto.request.ChatMessageRequest;
import alivium.model.dto.response.ChatMessageResponse;
import alivium.model.dto.response.MessageResponse;
import alivium.model.enums.ChatStatus;
import alivium.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @CacheEvict(value = "chatMessages", allEntries = true)
    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        ChatRoom chatRoom = findRoomById(request.getChatRoomId());
        if (chatRoom.getStatus() != ChatStatus.OPEN) {
            throw new BusinessException("Chat is not open for messaging");
        }
        User user = findUserById(request.getSenderId());

        checkUserAccess(chatRoom, user, "send messages");

        ChatMessage chatMessage = chatMessageMapper.toEntity(request, chatRoom, user);
        chatMessageRepository.save(chatMessage);
        return chatMessageMapper.toResponse(chatMessage);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "chatMessages", key = "#chatRoomId + '-' + #userId")
    public List<ChatMessageResponse> getMessagesByChatRoom(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = findRoomById(chatRoomId);
        User user = findUserById(userId);

        checkUserAccess(chatRoom, user, "view messages");

        List<ChatMessage> chatMessage = chatMessageRepository.findByChatRoomOrderBySentAtAsc(chatRoom);
        return chatMessage.stream()
                .map(chatMessageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "chatMessages", key = "'unread-' + #chatRoomId + '-' + #userId")
    public List<ChatMessageResponse> getUnreadMessagesByChatRoom(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = findRoomById(chatRoomId);
        User user = findUserById(userId);

        checkUserAccess(chatRoom, user, "get messages");

        List<ChatMessage> unread = chatMessageRepository.findByChatRoomAndReadFalseOrderBySentAtAsc(chatRoom);
        return unread.stream()
                .map(chatMessageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "chatMessages", allEntries = true)
    public ChatMessageResponse markAsRead(Long messageId, Long currentUserId) {
        ChatMessage message = findMessageById(messageId);
        User user = findUserById(currentUserId);
        ChatRoom chatRoom = message.getChatRoom();

        checkUserAccess(chatRoom, user, "read messages");

        if (!message.isRead()) {
            message.setRead(true);
            chatMessageRepository.save(message);
        }
        return chatMessageMapper.toResponse(message);
    }

    @Override
    @Transactional
    @CacheEvict(value = "chatMessages", allEntries = true)
    public ChatMessageResponse editMessage(Long messageId, String newMessage, Long currentUserId) {
        ChatMessage message = findMessageById(messageId);
        ChatRoom chatRoom = message.getChatRoom();
        User user = findUserById(currentUserId);

        if (chatRoom.getStatus() == ChatStatus.CLOSED) {
            throw new BusinessException("Cannot edit message in a closed chat");
        }
        if (message.getSender() == null || !user.equals(message.getSender())) {
            throw new BusinessException("You can edit only your own messages");
        }
        if (newMessage == null || newMessage.trim().isEmpty()) {
            throw new BusinessException("Message cannot be empty");
        }

        message.setMessage(newMessage.trim());
        chatMessageRepository.save(message);
        return chatMessageMapper.toResponse(message);
    }

    @Override
    @Transactional
    @CacheEvict(value = "chatMessages", allEntries = true)
    public MessageResponse deleteMessage(Long messageId, Long currentUserId) {
        ChatMessage message = findMessageById(messageId);
        User user = findUserById(currentUserId);
        ChatRoom chatRoom = message.getChatRoom();
        if (chatRoom.getStatus() == ChatStatus.CLOSED) {
            throw new BusinessException("Cannot delete message in a closed chat");
        }

        checkUserAccess(chatRoom, user, "delete messages");

        chatMessageRepository.delete(message);
        return MessageResponse.builder()
                .message("Message was deleted successfully")
                .build();
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    private ChatRoom findRoomById(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Chat Room not found with id: " + id));
    }

    private ChatMessage findMessageById(Long messageId) {
        return chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found with id: " + messageId));
    }

    private void checkUserAccess(ChatRoom chatRoom, User user, String action) {
        if ((!user.equals(chatRoom.getUser())) && (chatRoom.getAdmin() == null || !user.equals(chatRoom.getAdmin()))) {
            throw new BusinessException("You are not allowed to " + action + " in this chat");
        }
    }
}
