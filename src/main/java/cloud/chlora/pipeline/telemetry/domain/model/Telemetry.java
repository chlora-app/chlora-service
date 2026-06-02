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
        int batteryLevel,
        Instant receivedAt,
        boolean isValid
) {}