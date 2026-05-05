package cloud.chlora.pipeline.dashboard.adapter.in.event;

import cloud.chlora.pipeline.dashboard.application.service.DashboardSseService;
import cloud.chlora.pipeline.shared.event.SensorAnomalyDetectedEvent;
import cloud.chlora.pipeline.shared.event.TelemetryProcessedEvent;
import cloud.chlora.shared.event.DevicesMarkedOfflineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardEventListener {

    private final DashboardSseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void on(TelemetryProcessedEvent event) {
        if (!event.validationResult().valid()) {
            return;
        }
        log.info("[Dashboard] Received TelemetryProcessedEvent: device={}", event.telemetry().deviceId());
        sseService.broadcastSnapshot();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void on(SensorAnomalyDetectedEvent event) {
        log.info("[Dashboard] Received SensorAnomalyDetectedEvent: device={}", event.deviceId());
        sseService.broadcastSnapshot();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void on(DevicesMarkedOfflineEvent event) {
        log.info("[Dashboard] Received DevicesMarkedOfflineEvent: affected={}", event.affectedCount());
        sseService.broadcastSnapshot();
    }
}