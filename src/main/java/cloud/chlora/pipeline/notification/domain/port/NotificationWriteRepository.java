package cloud.chlora.pipeline.notification.domain.port;

import cloud.chlora.pipeline.notification.domain.model.Notification;

import java.time.Instant;

public interface NotificationWriteRepository {

    Notification save(Notification notification);
    int deleteOlderThan(Instant cutoff);
}