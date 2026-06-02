package cloud.chlora.pipeline.telemetry.adapter.in.event;

import cloud.chlora.pipeline.shared.event.TelemetryProcessedEvent;
import cloud.chlora.pipeline.telemetry.application.usecase.SaveTelemetryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryEventListener {

    private final SaveTelemetryUseCase useCase;

    @EventListener
    public void on(TelemetryProcessedEvent event) {
        log.info("[Telemetry] Listener received TelemetryProcessedEvent: device={}", event.telemetry().deviceId());
        useCase.execute(event.telemetry(), event.validationResult());
    }
}