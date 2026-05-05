package cloud.chlora.pipeline.telemetry.application.service;

import cloud.chlora.pipeline.shared.ValidationResult;
import cloud.chlora.pipeline.shared.event.ProcessedTelemetryEvent;
import cloud.chlora.pipeline.shared.event.TelemetrySavedEvent;
import cloud.chlora.pipeline.telemetry.application.usecase.SaveTelemetryUseCase;
import cloud.chlora.pipeline.telemetry.domain.model.Telemetry;
import cloud.chlora.pipeline.telemetry.domain.port.TelemetryWriteRepository;
import cloud.chlora.shared.port.DeviceRegistrationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService implements SaveTelemetryUseCase {

    private final DeviceRegistrationPort deviceRegistrationPort;
    private final TelemetryWriteRepository telemetryWriteRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void execute(ProcessedTelemetryEvent event, ValidationResult validationResult) {
        if (!deviceRegistrationPort.isDeviceRegistered(event.deviceId())) {
            deviceRegistrationPort.registerDevice(event.deviceId());
        }

        var telemetry = new Telemetry(
                null,
                event.deviceId(),
                event.deviceTimestamp(),
                event.soilMoisture(),
                event.temperature(),
                event.humidity(),
                event.batteryLevel(),
                event.receivedAt(),
                validationResult.valid()
        );

        var saved = telemetryWriteRepository.save(telemetry);

        eventPublisher.publishEvent(new TelemetrySavedEvent(
                saved.id(),
                saved.deviceId(),
                saved.soilMoisture(),
                saved.temperature(),
                saved.humidity()
        ));

        try {
            deviceRegistrationPort.setDeviceOnline(saved.deviceId());
        } catch (Exception e) {
            log.warn("[Telemetry] Failed to set device online deviceId={}", saved.deviceId(), e);
        }

        log.info("[Telemetry] Saved device={} isValid={}", saved.deviceId(), saved.isValid());
    }
}