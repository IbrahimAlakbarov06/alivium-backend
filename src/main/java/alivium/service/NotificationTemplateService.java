package alivium.service;

import alivium.domain.entity.User;
import alivium.model.enums.NotificationTemplate;

import java.util.Map;

public interface NotificationTemplateService {
    void sendNotification(User user, NotificationTemplate template, Map<String,String> params, String image);

    void sendNotification(User user, NotificationTemplate template, Map<String,String> params);

    void sendNotificationToAll(NotificationTemplate template, Map<String,String> params, String image);

    void sendNotificationToAll(NotificationTemplate template, Map<String,String> params);
}
