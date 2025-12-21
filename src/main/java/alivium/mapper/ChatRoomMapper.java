package alivium.mapper;

import alivium.domain.entity.ChatRoom;
import alivium.domain.entity.User;
import alivium.model.dto.request.ChatRoomRequest;
import alivium.model.dto.response.ChatRoomResponse;
import alivium.model.enums.ChatStatus;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomMapper {

//    public ChatRoom toEntity(User user) {
//        if (request == null) return null;
//
//        return ChatRoom.builder()
//                .user(user)
//                .admin(null)
//                .status(ChatStatus.OPEN)
//                .build();
//    }

    public ChatRoomResponse toResponse(ChatRoom chatRoom) {
        if (chatRoom == null) return null;

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .userId(chatRoom.getUser().getId())
                .userFullName(chatRoom.getUser().getFullName())
                .adminId(chatRoom.getAdmin().getId())
                .adminFullName(chatRoom.getAdmin().getFullName())
                .createdAt(chatRoom.getCreatedAt())
                .status(chatRoom.getStatus())
                .build();

    }
}
