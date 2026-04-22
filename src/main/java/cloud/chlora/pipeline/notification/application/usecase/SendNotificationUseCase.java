package cloud.chlora.pipeline.notification.application.usecase;

import cloud.chlora.pipeline.notification.adapter.out.sse.SseNotificationAdapter;
import cloud.chlora.pipeline.notification.application.port.out.NotificationSenderPort;
import cloud.chlora.pipeline.notification.application.service.NotificationRateLimiter;
import cloud.chlora.pipeline.notification.domain.model.BatteryNotificationThreshold;
import cloud.chlora.pipeline.notification.domain.model.Notification;
import cloud.chlora.pipeline.notification.domain.port.NotificationWriteRepository;
import cloud.chlora.pipeline.shared.NotificationMessage;
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
        // Reset rate limiter jika battery sudah di-charge (kembali di atas semua threshold)
        rateLimiter.resetIfCharged(event.deviceId(), event.batteryLevel());

        BatteryNotificationThreshold.resolve(event.batteryLevel())
                .ifPresent(threshold -> {
                    if (rateLimiter.tryAcquire(event.deviceId(), threshold)) {
                        log.info("[Notification] Battery {} triggered for device={}, level={}%",
                                threshold, event.deviceId(), event.batteryLevel());
                        process(formatter.formatLowBattery(event, threshold));
                    }
                });
    }

    @Transactional
    public void handleAnomaly(SensorAnomalyDetectedEvent event) {
        process(formatter.formatAnomaly(event));
    }

    private void process(Notification notification) {
        // 1. Save notification
        Notification saved = notificationWriteRepository.save(notification);
        log.info("[Notification] Saved: {}", saved.notificationId());

        // 2. Fan-out ke semua user aktif
        List<String> userIds = registeredUserReadPort.findAllActiveUserIds();
        statusWriteRepository.fanOut(saved, userIds);
        log.info("[Notification] Fanned out to {} users", userIds.size());

        // 3. Kirim Telegram
        var message = new NotificationMessage(
                saved.notificationType().name(),
                saved.message(),
                saved.severity(),
                saved.notificationType()
        );
        notificationSenderPort.send(message);

        // 4. Kirim SSE setelah transaction commit — mencegah race condition
        //    di mana client menerima event lalu langsung GET /notifications
        //    sebelum data tersimpan di DB.
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sseNotificationAdapter.notifyUsers(saved, userIds);
            }
        });
    }
}