package alivium.service;

import alivium.model.dto.request.ChatMessageRequest;
import alivium.model.dto.response.ChatMessageResponse;
import alivium.model.dto.response.MessageResponse;

import java.util.List;

public interface ChatMessageService {
    ChatMessageResponse sendMessage(ChatMessageRequest request);

    List<ChatMessageResponse> getMessagesByChatRoom(Long chatRoomId,Long userId);

    List<ChatMessageResponse> getUnreadMessagesByChatRoom(Long chatRoomId,Long userId);

    ChatMessageResponse markAsRead(Long messageId,Long currentUserId);

    ChatMessageResponse editMessage(Long messageId, String newMessage,Long currentUserId);

    MessageResponse deleteMessage(Long messageId,Long currentUserId);
}
