package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.response.ChatRoomResponse;
import alivium.model.enums.ChatStatus;
import alivium.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatRooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ResponseEntity<ChatRoomResponse> createChatRoom(@AuthenticationPrincipal User user) {
        ChatRoomResponse response = chatRoomService.createChatRoom(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ChatRoomResponse>> getMyChatRooms(@AuthenticationPrincipal User user) {
        List<ChatRoomResponse> response = chatRoomService.getMyChatRooms(user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{chatRoomId}/assignAdmin")
    @PreAuthorize("hasRole('ADMIN_ROLE') or hasRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<ChatRoomResponse> assignAdmin(@PathVariable Long chatRoomId,
                                                        @RequestParam Long adminId) {
        ChatRoomResponse response = chatRoomService.assignAdmin(chatRoomId, adminId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{chatRoomId}/close")
    public ResponseEntity<ChatRoomResponse> closeChat(@PathVariable Long chatRoomId,
                                                      @AuthenticationPrincipal User user) {
        ChatRoomResponse response = chatRoomService.closeChat(chatRoomId,user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomResponse> getById(@PathVariable Long chatRoomId) {
        ChatRoomResponse response = chatRoomService.getById(chatRoomId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{chatRoomId}/resolve")
    public ResponseEntity<ChatRoomResponse> markResolved(@PathVariable Long chatRoomId,
                                                         @AuthenticationPrincipal User user) {
        ChatRoomResponse response = chatRoomService.markResolved(chatRoomId, user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my/status")
    public ResponseEntity<List<ChatRoomResponse>> getMyChatRoomsByStatus(
            @AuthenticationPrincipal User user,
            @RequestParam ChatStatus status) {
        List<ChatRoomResponse> response = chatRoomService.getChatRoomsByStatus(user.getId(), status);
        return ResponseEntity.ok(response);
    }

}

