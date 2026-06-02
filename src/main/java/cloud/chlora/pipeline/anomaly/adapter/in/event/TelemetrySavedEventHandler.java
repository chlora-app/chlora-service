package cloud.chlora.pipeline.anomaly.adapter.in.event;

import cloud.chlora.pipeline.anomaly.application.usecase.UpdateAnomalyUseCase;
import cloud.chlora.pipeline.anomaly.domain.model.DetectionRequest;
import cloud.chlora.pipeline.shared.event.TelemetrySavedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetrySavedEventHandler {

    private final UpdateAnomalyUseCase updateAnomalyUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TelemetrySavedEvent event) {
        log.info("[Anomaly] Received event telemetryId={} deviceId={}", event.telemetryId(), event.deviceId());

        DetectionRequest request = DetectionRequest.builder()
                .deviceId(event.deviceId())
                .soilMoisture(event.soilMoisture())
                .temperature(event.temperature())
                .humidity(event.humidity())
                .build();

        updateAnomalyUseCase.update(request, event.telemetryId());
    }
}
