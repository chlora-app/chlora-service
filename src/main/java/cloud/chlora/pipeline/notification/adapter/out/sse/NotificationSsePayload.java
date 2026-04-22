package cloud.chlora.pipeline.notification.adapter.out.sse;

import cloud.chlora.pipeline.shared.NotificationSeverity;
import cloud.chlora.pipeline.shared.NotificationType;

import java.time.Instant;

public record NotificationSsePayload(
        long unreadCount,
        NotificationSummary notification
) {
    public record NotificationSummary(
            String id,
            String message,
            NotificationSeverity severity,
            NotificationType notificationType,
            Instant time, // TODO: Use Instant
            boolean isRead
    ) {}
}