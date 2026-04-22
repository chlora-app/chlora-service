package cloud.chlora.pipeline.notification.application.usecase;

import cloud.chlora.pipeline.notification.adapter.in.web.response.NotificationListResponse;
import cloud.chlora.pipeline.notification.adapter.in.web.response.NotificationListResponse.NotificationItem;
import cloud.chlora.pipeline.notification.domain.port.NotificationReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class GetNotificationListUseCase {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("HH:mm d MMM yyyy", Locale.ENGLISH)
            .withZone(ZoneOffset.UTC);

    private final NotificationReadRepository notificationReadRepository;

    public NotificationListResponse execute(String userId) {
        List<NotificationItem> items = notificationReadRepository.findAllByUserId(userId)
                .stream()
                .map(n -> new NotificationItem(
                        n.notificationId(),
                        n.message(),
                        n.createdAt(),
                        n.notificationType(),
                        n.isRead()
                ))
                .toList();

        return new NotificationListResponse(items);
    }
}