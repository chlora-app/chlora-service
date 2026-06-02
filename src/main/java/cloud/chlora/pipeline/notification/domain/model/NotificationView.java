package cloud.chlora.pipeline.notification.domain.model;

import cloud.chlora.pipeline.shared.NotificationSeverity;
import cloud.chlora.pipeline.shared.NotificationType;

import java.time.Instant;

public record NotificationView(
        String notificationId,
        String message,
        NotificationSeverity severity,
        NotificationType notificationType,
        Instant createdAt,
        boolean isRead
) {}