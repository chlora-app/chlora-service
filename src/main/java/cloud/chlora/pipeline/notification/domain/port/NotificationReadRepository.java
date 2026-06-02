package cloud.chlora.pipeline.notification.domain.port;

import cloud.chlora.pipeline.notification.domain.model.NotificationView;

import java.util.List;

public interface NotificationReadRepository {

    List<NotificationView> findAllByUserId(String userId);
}