package cloud.chlora.pipeline.shared;

public record NotificationMessage(
        String title,
        String body,
        NotificationSeverity severity,
        NotificationType notificationType
) {}