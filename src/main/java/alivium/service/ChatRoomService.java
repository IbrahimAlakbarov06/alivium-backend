package alivium.service;

import alivium.model.dto.response.ChatRoomResponse;
import alivium.model.enums.ChatStatus;

import java.util.List;

public interface ChatRoomService {
    ChatRoomResponse createChatRoom(Long userId);
    List<ChatRoomResponse> getMyChatRooms(Long currentUserId);
    ChatRoomResponse assignAdmin(Long chatRoomId, Long adminId);
    ChatRoomResponse closeChat(Long chatRoomId,Long currentUser);
    ChatRoomResponse getById(Long chatRoomId);
    ChatRoomResponse markResolved(Long chatRoomId, Long userId);
    List<ChatRoomResponse> getChatRoomsByStatus(Long userId, ChatStatus status);
}
