package alivium.service;

import alivium.model.dto.response.ChatRoomResponse;

import java.util.List;

public interface ChatRoomService {
    ChatRoomResponse createChatRoom(Long userId);
    List<ChatRoomResponse> getMyChatRooms(Long currentUserId);
    ChatRoomResponse assignAdmin(Long chatRoomId, Long adminId);
    ChatRoomResponse closeChat(Long chatRoomId);
    ChatRoomResponse getById(Long chatRoomId);
}
