package cloud.chlora.pipeline.validation.adapter.in;

import cloud.chlora.pipeline.ingestion.internal.MqttPayload;
import cloud.chlora.pipeline.shared.ValidationResult;
import cloud.chlora.pipeline.shared.event.*;
import cloud.chlora.pipeline.shared.port.ValidationPort;
import cloud.chlora.pipeline.validation.application.validator.TelemetryValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationPortAdapter implements ValidationPort {

    private final TelemetryValidator validator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ValidationResult validate(MqttPayload payload) {
        ValidationResult result = validator.validate(payload);
        if (result.isInvalid()) {
            log.warn("[Validation] Invalid payload: {}", result.reason());
        }

        var telemetryEvent = ProcessedTelemetryEvent.builder()
                .deviceId(payload.data().deviceId())
                .deviceTimestamp(Instant.ofEpochMilli(payload.data().timestamp()))
                .soilMoisture(payload.data().soilMoisture())
                .temperature(payload.data().temperature())
                .humidity(payload.data().humidity())
                .batteryLevel(payload.data().batteryLevel())
                .receivedAt(payload.metadata().receivedAt())
                .build();

        log.info("[Validation] Publishing TelemetryProcessedEvent for device={} isValid={}",
                telemetryEvent.deviceId(), result.valid());

        eventPublisher.publishEvent(new TelemetryProcessedEvent(telemetryEvent, result));

        if (result.isInvalid()) {
            publishAnomalyEvents(payload, telemetryEvent);
        }

        return result;
    }

    private void publishAnomalyEvents(MqttPayload payload, ProcessedTelemetryEvent telemetry) {
        var data = payload.data();

        checkAndPublish(
                data.temperature(), -40f, 85f, AnomalyType.TEMPERATURE_OUT_OF_RANGE, AnomalySeverity.HIGH, telemetry
        );

        checkAndPublish(
                data.humidity(), 0f, 100f, AnomalyType.HUMIDITY_OUT_OF_RANGE, AnomalySeverity.MEDIUM, telemetry
        );

        checkAndPublish(
                data.soilMoisture(), 0f, 100f, AnomalyType.SOIL_MOISTURE_OUT_OF_RANGE, AnomalySeverity.MEDIUM, telemetry
        );
    }

    private void checkAndPublish(
            float value, float min, float max,
            AnomalyType type, AnomalySeverity severity,
            ProcessedTelemetryEvent telemetry
    ) {
        if (value < min || value > max) {
            eventPublisher.publishEvent(new SensorAnomalyDetectedEvent(
                    telemetry.deviceId(),
                    telemetry.deviceTimestamp(),
                    Instant.now(),
                    type,
                    severity,
                    "%s value %.2f out of range [%.1f, %.1f]".formatted(type.name(), value, min, max),
                    value,
                    min,
                    max
            ));
        }
    }
}