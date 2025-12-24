package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.response.ChatMessageResponse;
import alivium.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-messages")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @GetMapping("/room/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal User user
    ) {
        List<ChatMessageResponse> messages= chatMessageService.getMessagesByChatRoom(chatRoomId, user.getId());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/room/{chatRoomId}/unread")
    public ResponseEntity<List<ChatMessageResponse>> getUnreadMessages(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal User user
    ) {
        List<ChatMessageResponse> messages= chatMessageService.getUnreadMessagesByChatRoom(chatRoomId, user.getId());
        return ResponseEntity.ok(messages);
    }
}
