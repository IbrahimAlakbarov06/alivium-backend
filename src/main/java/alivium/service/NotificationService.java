package alivium.service;

import alivium.model.dto.request.NotificationRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(NotificationRequest request);

    List<NotificationResponse> getMyNotifications(Long userId);

    List<NotificationResponse> getUnreadNotifications(Long userId);

    Long getUnreadCount(Long userId);

    NotificationResponse markAsRead(Long userId, Long notificationId);

    MessageResponse markAllAsRead(Long userId);

    MessageResponse deleteNotification(Long userId, Long notificationId);

    MessageResponse deleteAllNotification(Long userId);

    MessageResponse saveFCMToken(Long userId, String fcmToken);

    MessageResponse removeFCMToken(Long userId, String fcmToken);
}