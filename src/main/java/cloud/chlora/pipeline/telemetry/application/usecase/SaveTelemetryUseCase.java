package cloud.chlora.pipeline.telemetry.application.usecase;

import cloud.chlora.pipeline.shared.ValidationResult;
import cloud.chlora.pipeline.shared.event.ProcessedTelemetryEvent;
import cloud.chlora.pipeline.telemetry.domain.model.Telemetry;
import cloud.chlora.pipeline.telemetry.domain.port.TelemetryWriteRepository;
import cloud.chlora.shared.port.DeviceRegistrationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveTelemetryUseCase {

    private final DeviceRegistrationPort deviceRegistrationPort;
    private final TelemetryWriteRepository telemetryWriteRepository;

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

        telemetryWriteRepository.save(telemetry);

        try {
            deviceRegistrationPort.setDeviceOnline(telemetry.deviceId());
        } catch (Exception e) {
            log.warn("[Telemetry] Failed to set device online deviceId={}", telemetry.deviceId(), e);
        }

        log.info("[Telemetry] Saved device={} isValid={}", telemetry.deviceId(), telemetry.isValid());
    }
}