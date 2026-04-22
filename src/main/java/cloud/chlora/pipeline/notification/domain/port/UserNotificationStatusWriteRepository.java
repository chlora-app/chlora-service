package cloud.chlora.pipeline.notification.domain.port;

import cloud.chlora.pipeline.notification.domain.model.Notification;

import java.util.List;

public interface UserNotificationStatusWriteRepository {

    void fanOut(Notification notification, List<String> userIds);

    void markAsRead(String notificationId, String userId);

    void markAllAsRead(String userId);
}