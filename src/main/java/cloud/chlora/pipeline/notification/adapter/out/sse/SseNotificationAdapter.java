package cloud.chlora.pipeline.notification.adapter.out.sse;

import cloud.chlora.pipeline.notification.domain.model.Notification;
import cloud.chlora.pipeline.notification.domain.port.UserNotificationStatusReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SseNotificationAdapter {

    private final SseEmitterRegistry registry;
    private final UserNotificationStatusReadRepository statusReadRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("HH:mm d MMM yyyy", Locale.ENGLISH)
            .withZone(ZoneOffset.UTC);

    public void notifyUsers(Notification notification, List<String> userIds) {
        var summary = new NotificationSsePayload.NotificationSummary(
                notification.notificationId(),
                notification.message(),
                notification.severity(),
                notification.notificationType(),
                notification.createdAt(),
                false
        );

        userIds.forEach(userId -> {
            long unreadCount = statusReadRepository.countUnread(userId);
            var payload = new NotificationSsePayload(unreadCount, summary);
            registry.sendToUser(userId, "notification", payload);
        });
    }
}