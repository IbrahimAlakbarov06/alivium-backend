package alivium.mapper;

import alivium.domain.entity.ChatMessage;
import alivium.domain.entity.ChatRoom;
import alivium.domain.entity.User;
import alivium.model.dto.request.ChatMessageRequest;
import alivium.model.dto.response.ChatMessageResponse;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public ChatMessage toEntity(ChatMessageRequest request, ChatRoom chatRoom, User sender){
        if (request == null) return null;

        return ChatMessage.builder()
                .message(request.getMessage())
                .chatRoom(chatRoom)
                .sender(sender)
                .read(false)
                .build();
    }

    public ChatMessageResponse toResponse(ChatMessage message){
        if (message == null) return null;

        User sender = message.getSender();

        return ChatMessageResponse.builder()
                .id(message.getId())
                .message(message.getMessage())
                .chatRoomId(message.getChatRoom().getId())
                .senderId(sender != null ? sender.getId() : null)
                .senderFullName(sender != null ? sender.getFullName() : "System")
                .read(message.isRead())
                .sentAt(message.getSentAt())
                .build();
    }
}
