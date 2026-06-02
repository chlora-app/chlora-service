package cloud.chlora.pipeline.notification.adapter.out.persistence.repository;

public interface NotificationListProjection {

    String getNotificationId();
    String getMessage();
    String getSeverity();
    String getNotificationType();
    java.time.Instant getCreatedAt();
    boolean getIsRead();
}