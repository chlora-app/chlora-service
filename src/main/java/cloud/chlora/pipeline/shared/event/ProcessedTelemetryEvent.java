package cloud.chlora.pipeline.shared.event;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ProcessedTelemetryEvent(
        String deviceId,
        Instant deviceTimestamp,
        float soilMoisture,
        float temperature,
        float humidity,
        float batteryLevel,
        Instant receivedAt
) { }
