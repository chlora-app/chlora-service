package cloud.chlora.pipeline.notification.adapter.in.event;

import cloud.chlora.pipeline.notification.application.usecase.SendNotificationUseCase;
import cloud.chlora.pipeline.shared.event.SensorAnomalyDetectedEvent;
import cloud.chlora.pipeline.shared.event.TelemetryProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final SendNotificationUseCase useCase;

    @EventListener
    void on(TelemetryProcessedEvent event) {
        if (!event.validationResult().valid()) {
            return;
        }
        log.info("[Notification] Listener received TelemetryProcessedEvent: device={}",
                event.telemetry().deviceId());
        useCase.handle(event.telemetry());
    }

    @EventListener
    void on(SensorAnomalyDetectedEvent event) {
        log.info("[Notification] Listener received SensorAnomalyDetectedEvent: {}", event);
        useCase.handleAnomaly(event);
    }
}