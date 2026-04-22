package cloud.chlora.pipeline.telemetry.domain.model;

import java.time.Instant;

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