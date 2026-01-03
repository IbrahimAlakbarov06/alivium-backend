package alivium.service.impl;

import alivium.domain.entity.Notification;
import alivium.domain.entity.User;
import alivium.domain.repository.NotificationRepository;
import alivium.domain.repository.UserRepository;
import alivium.exception.NotFoundException;
import alivium.mapper.NotificationMapper;
import alivium.model.dto.request.NotificationRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.NotificationResponse;
import alivium.model.enums.NotificationStatus;
import alivium.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    @CacheEvict(value = "notifications", key = "#request.userId")
    public NotificationResponse createNotification(NotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getUserId()));

        Notification notification = notificationMapper.toEntity(request, user);
        Notification saved = notificationRepository.save(notification);

        return notificationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "notifications", key = "#userId")
    public List<NotificationResponse> getMyNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notificationMapper.toListResponse(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "notifications", key = "'unread-' + #userId")
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD);
        return notificationMapper.toListResponse(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notifications", key = "#userId")
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification notification = findByIdAndUserId(notificationId, userId);

        if (notification.getStatus() == NotificationStatus.UNREAD) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notifications", key = "#userId")
    public MessageResponse markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD);

        LocalDateTime now = LocalDateTime.now();
        for (Notification notification : unreadNotifications) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(now);
        }

        notificationRepository.saveAll(unreadNotifications);

        return new MessageResponse("All notifications marked as read");
    }

    @Override
    @Transactional
    @CacheEvict(value = "notifications", key = "#userId")
    public MessageResponse deleteNotification(Long userId, Long notificationId) {
        Notification notification = findByIdAndUserId(notificationId, userId);
        notificationRepository.delete(notification);

        return new MessageResponse("Notification deleted successfully");
    }

    @Override
    @Transactional
    @CacheEvict(value = "notifications", key = "#userId")
    public MessageResponse deleteAllNotification(Long userId) {
        notificationRepository.deleteByUserId(userId);

        return new MessageResponse("Notification deleted successfully");
    }

    @Override
    @Transactional
    public MessageResponse saveFCMToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        user.setFcmToken(fcmToken);
        userRepository.save(user);

        return new MessageResponse("FCM token registered successfully");
    }

    @Override
    @Transactional
    public MessageResponse removeFCMToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (fcmToken.equals(user.getFcmToken())) {
            user.setFcmToken(null);
            userRepository.save(user);
        }

        return new MessageResponse("FCM token removed successfully");
    }

    private Notification findByIdAndUserId(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new NotFoundException("Notification not found");
        }

        return notification;
    }
}