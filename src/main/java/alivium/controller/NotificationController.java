package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.request.FCMTokenRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.NotificationResponse;
import alivium.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/my")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(@AuthenticationPrincipal User user){
        List<NotificationResponse> responses =notificationService.getMyNotifications(user.getId());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(@AuthenticationPrincipal User user){
        List<NotificationResponse> responses =notificationService.getUnreadNotifications(user.getId());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("unread/count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal User user){
        Long unreadCount =notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(unreadCount);
    }

    @PutMapping("{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id, @AuthenticationPrincipal User user){
        NotificationResponse response=notificationService.markAsRead(user.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("read-all")
    public ResponseEntity<MessageResponse> markAllAsRead(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(notificationService.markAllAsRead(user.getId()));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<MessageResponse> deleteNotification(@PathVariable Long id, @AuthenticationPrincipal User user){
        return ResponseEntity.ok(notificationService.deleteNotification(user.getId(), id));
    }

    @DeleteMapping("/all")
    public ResponseEntity<MessageResponse> deleteAllNotifications(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.deleteAllNotification(user.getId()));
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<MessageResponse> registerFCMToken(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody FCMTokenRequest request) {
        return ResponseEntity.ok(notificationService.saveFCMToken(user.getId(), request.getFcmToken()));
    }

    @DeleteMapping("/fcm-token")
    public ResponseEntity<MessageResponse> removeFCMToken(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody FCMTokenRequest request) {
        return ResponseEntity.ok(notificationService.removeFCMToken(user.getId(), request.getFcmToken()));
    }
}
