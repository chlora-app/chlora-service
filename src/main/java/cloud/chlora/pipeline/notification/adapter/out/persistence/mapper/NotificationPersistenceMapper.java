package cloud.chlora.pipeline.notification.adapter.out.persistence.mapper;

import cloud.chlora.pipeline.notification.adapter.out.persistence.entity.NotificationWriteEntity;
import cloud.chlora.pipeline.notification.domain.model.Notification;

public final class NotificationPersistenceMapper {

    private NotificationPersistenceMapper() {}

    public static Notification toDomain(NotificationWriteEntity e) {
        return new Notification(
                e.getId(),
                e.getNotificationId(),
                e.getDeviceId(),
                e.getMessage(),
                e.getSeverity(),
                e.getNotificationType(),
                e.getCreatedAt()
        );
    }

    public static NotificationWriteEntity toEntity(Notification n) {
        return NotificationWriteEntity.builder()
                .deviceId(n.deviceId())
                .message(n.message())
                .severity(n.severity())
                .notificationType(n.notificationType())
                .build();
    }
}