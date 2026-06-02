package cloud.chlora.pipeline.notification.domain.port;

public interface UserNotificationStatusReadRepository {

    long countUnread(String userId);
}