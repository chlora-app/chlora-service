package cloud.chlora.pipeline.notification.application.usecase;

import cloud.chlora.pipeline.notification.adapter.out.sse.SseNotificationAdapter;
import cloud.chlora.pipeline.notification.application.port.out.NotificationSenderPort;
import cloud.chlora.pipeline.notification.application.service.NotificationRateLimiter;
import cloud.chlora.pipeline.notification.domain.model.BatteryNotificationThreshold;
import cloud.chlora.pipeline.notification.domain.model.Notification;
import cloud.chlora.pipeline.notification.domain.port.NotificationWriteRepository;
import cloud.chlora.pipeline.shared.NotificationMessage;
import cloud.chlora.shared.enums.AnomalySeverity;
import cloud.chlora.shared.port.RegisteredUserReadPort;
import cloud.chlora.pipeline.notification.domain.port.UserNotificationStatusWriteRepository;
import cloud.chlora.pipeline.notification.internal.NotificationMessageFormatter;
import cloud.chlora.pipeline.shared.event.ProcessedTelemetryEvent;
import cloud.chlora.pipeline.shared.event.SensorAnomalyDetectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendNotificationUseCase {

    private final NotificationMessageFormatter formatter;
    private final NotificationWriteRepository notificationWriteRepository;
    private final UserNotificationStatusWriteRepository statusWriteRepository;
    private final RegisteredUserReadPort registeredUserReadPort;
    private final SseNotificationAdapter sseNotificationAdapter;
    private final NotificationRateLimiter rateLimiter;
    private final NotificationSenderPort notificationSenderPort;

    @Transactional
    public void handle(ProcessedTelemetryEvent event) {
        rateLimiter.resetIfCharged(event.deviceId(), Math.round(event.batteryLevel()));

        BatteryNotificationThreshold.resolve(Math.round(event.batteryLevel()))
                .ifPresent(threshold -> {
                    if (rateLimiter.tryAcquire(event.deviceId(), threshold)) {
                        log.info("[Notification] Battery {} triggered for device={}, level={}%", threshold, event.deviceId(), event.batteryLevel());
                        process(formatter.formatLowBattery(event, threshold));
                    }
                });
    }

    @Transactional
    public void handleAnomaly(SensorAnomalyDetectedEvent event) {
        Notification notification = formatter.formatAnomaly(event);
        boolean shouldNotify = event.severity() != AnomalySeverity.LOW;

        process(notification, shouldNotify);
    }

    private void process(Notification notification) {
        process(notification, true);
    }

    private void process(Notification notification, boolean shouldNotify) {
        Notification saved = notificationWriteRepository.save(notification);
        log.info("[Notification] Saved: {}", saved.notificationId());

        List<String> userIds = registeredUserReadPort.findAllActiveUserIds();
        statusWriteRepository.fanOut(saved, userIds);
        log.info("[Notification] Fanned out to {} users", userIds.size());

        if (shouldNotify) {
            var message = new NotificationMessage(
                    saved.notificationType().name(),
                    saved.message(),
                    saved.severity(),
                    saved.notificationType()
            );
            notificationSenderPort.send(message);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sseNotificationAdapter.notifyUsers(saved, userIds);
                }
            });
        }
    }
}