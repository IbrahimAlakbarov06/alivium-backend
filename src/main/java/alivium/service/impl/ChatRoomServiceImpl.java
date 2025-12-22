package alivium.service.impl;

import alivium.domain.entity.ChatMessage;
import alivium.domain.entity.ChatRoom;
import alivium.domain.entity.User;
import alivium.domain.repository.ChatMessageRepository;
import alivium.domain.repository.ChatRoomRepository;
import alivium.domain.repository.UserRepository;
import alivium.exception.BusinessException;
import alivium.exception.NotFoundException;
import alivium.mapper.ChatRoomMapper;
import alivium.model.dto.response.ChatRoomResponse;
import alivium.model.enums.ChatStatus;
import alivium.model.enums.UserRole;
import alivium.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMapper chatRoomMapper;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    @Override
    public ChatRoomResponse createChatRoom(Long userId) {
        User user = findUserById(userId);

        if(chatRoomRepository.existsByUserAndStatus(user,ChatStatus.OPEN)){
            throw new BusinessException("User already has an open chat room");
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .user(user)
                .admin(null)
                .status(ChatStatus.OPEN)
                .build();
        chatRoomRepository.save(chatRoom);

        ChatMessage welcomeMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(null)
                .message("Hello! Welcome to support. How can we help you?")
                .read(true)
                .build();
        chatMessageRepository.save(welcomeMessage);

        return chatRoomMapper.toResponse(chatRoom);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "chatRooms", key = "#currentUserId")
    @Override
    public List<ChatRoomResponse> getMyChatRooms(Long currentUserId) {
        User user = findUserById(currentUserId);

        List<ChatRoom> list = new ArrayList<>();
        if (user.getRole() == UserRole.ADMIN_ROLE) {
            list = chatRoomRepository.findByAdmin(user);
        } else if (user.getRole() == UserRole.USER_ROLE) {
            list = chatRoomRepository.findByUser(user);
        }

        return list.stream()
                .map(chatRoomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "chatRooms", allEntries = true)
    @Override
    public ChatRoomResponse assignAdmin(Long chatRoomId, Long adminId) {
        ChatRoom chatRoom = findRoomById(chatRoomId);

        if(chatRoom.getStatus()==ChatStatus.CLOSED){
            throw new BusinessException("Cannot assign admin to a closed chat");
        }
        User admin = findUserById(adminId);
        if (admin.getRole() != UserRole.ADMIN_ROLE) {
            throw new BusinessException("User with id " + adminId + " is not an admin");
        }

        chatRoom.setAdmin(admin);
        ChatRoom updatedChatRoom = chatRoomRepository.save(chatRoom);
        return chatRoomMapper.toResponse(updatedChatRoom);
    }

    @Transactional
    @CacheEvict(value = "chatRooms", allEntries = true)
    @Override
    public ChatRoomResponse closeChat(Long chatRoomId,Long currentUserId) {
        ChatRoom chatRoom = findRoomById(chatRoomId);
        if (chatRoom.getStatus() == ChatStatus.CLOSED) {
            throw new BusinessException("Chat room is already closed");
        }

        User currentUser = findUserById(currentUserId);
        if (!currentUser.equals(chatRoom.getAdmin())
                && currentUser.getRole() != UserRole.ADMIN_ROLE) {
            throw new BusinessException("You are not allowed to close this chat");
        }

        chatRoom.setStatus(ChatStatus.CLOSED);
        ChatRoom updatedChatRoom = chatRoomRepository.save(chatRoom);
        return chatRoomMapper.toResponse(updatedChatRoom);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "chatRooms", key = "#chatRoomId")
    @Override
    public ChatRoomResponse getById(Long chatRoomId) {
        ChatRoom chatRoom = findRoomById(chatRoomId);
        return chatRoomMapper.toResponse(chatRoom);
    }

    @Override
    public ChatRoomResponse markResolved(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = findRoomById(chatRoomId);

        if(!chatRoom.getUser().getId().equals(userId)){
            throw new BusinessException("You are not the owner of this chat");
        }
        if (chatRoom.getStatus() != ChatStatus.OPEN) {
            throw new BusinessException("Chat cannot be resolved in current state");
        }
        chatRoom.setStatus(ChatStatus.USER_RESOLVED);
        return chatRoomMapper.toResponse(chatRoomRepository.save(chatRoom));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatRoomResponse> getChatRoomsByStatus(Long userId, ChatStatus status) {
        User user = findUserById(userId);

        List<ChatRoom> list = new ArrayList<>();
        if (user.getRole() == UserRole.SUPER_ADMIN_ROLE) {
            list = chatRoomRepository.findByStatus(status);
        } else if (user.getRole() == UserRole.ADMIN_ROLE) {
            list = chatRoomRepository.findByAdminAndStatus(user, status);
        } else if (user.getRole() == UserRole.USER_ROLE) {
            list = chatRoomRepository.findByUserAndStatus(user, status);
        }

        return list.stream()
                .map(chatRoomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    @CacheEvict(value = "chatRooms", allEntries = true)
    public void autoCloseInactiveChats(){
        List<ChatRoom> openRooms=chatRoomRepository.findByStatus(ChatStatus.OPEN);
        for(ChatRoom room:openRooms){
            ChatMessage last=chatMessageRepository.findTopByChatRoomOrderBySentAtDesc(room);
            if(last!=null
                    && last.getSender()!=null
                    && !last.getSender().equals(room.getUser())
                    && last.getSentAt().isBefore(LocalDateTime.now().minusMinutes(10))){
                room.setStatus(ChatStatus.CLOSED);
                chatRoomRepository.save(room);
            }
        }
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    private ChatRoom findRoomById(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Chat Room not found with id: " + id));
    }
}
