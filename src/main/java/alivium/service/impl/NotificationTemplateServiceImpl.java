package alivium.service.impl;

import alivium.domain.entity.Notification;
import alivium.domain.entity.User;
import alivium.domain.repository.UserRepository;
import alivium.model.dto.request.NotificationRequest;
import alivium.model.enums.NotificationTemplate;
import alivium.model.enums.UserStatus;
import alivium.service.NotificationService;
import alivium.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Override
    @Async
    public void sendNotification(User user, NotificationTemplate template, Map<String,String> params, String image) {
        try {
            String title= template.formatTitle(params);
            String message= template.formatMessage(params);

            NotificationRequest request =NotificationRequest.builder()
                    .userId(user.getId())
                    .title(title)
                    .message(message)
                    .type(template.getType())
                    .imageUrl(image)
                    .build();

            notificationService.createNotification(request);
        } catch (Exception e) {

        }
    }

    @Override
    @Async
    public void sendNotification(User user, NotificationTemplate template, Map<String,String> params){
        sendNotification(user, template, params, null);
    }

    @Override
    @Async
    public void sendNotificationToAll(NotificationTemplate template, Map<String,String> params, String image){
        List<User> activeUsers= userRepository.findByStatus(UserStatus.ACTIVE);

        for (User user : activeUsers) {
            sendNotification(user, template, params, image);
        }
    }

    @Override
    @Async
    public void sendNotificationToAll(NotificationTemplate template, Map<String,String> params){
        sendNotificationToAll(template, params, null);
    }
}
