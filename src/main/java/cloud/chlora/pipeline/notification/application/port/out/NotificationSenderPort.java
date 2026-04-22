package cloud.chlora.pipeline.notification.application.port.out;

import cloud.chlora.pipeline.shared.NotificationMessage;

public interface NotificationSenderPort {

    void send(NotificationMessage message);
}
