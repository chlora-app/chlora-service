package cloud.chlora.pipeline.notification.adapter.in.web.response;

import cloud.chlora.pipeline.shared.NotificationType;

import java.time.Instant;
import java.util.List;

public record NotificationListResponse(List<NotificationItem> notifications) {

    public record NotificationItem(
            String id,
            String message,
            Instant time,
            NotificationType notificationType,
            boolean isRead
    ) {}
}