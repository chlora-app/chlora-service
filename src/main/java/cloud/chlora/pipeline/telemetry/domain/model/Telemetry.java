package cloud.chlora.pipeline.telemetry.domain.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record Telemetry(
        Long id,
        String deviceId,
        Instant deviceTimestamp,
        float soilMoisture,
        float temperature,
        float humidity,
        float batteryLevel,
        Instant receivedAt,
        boolean isValid
) {}