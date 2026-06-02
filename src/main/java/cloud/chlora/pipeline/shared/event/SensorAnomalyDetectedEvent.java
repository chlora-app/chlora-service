package cloud.chlora.pipeline.shared.event;

import cloud.chlora.shared.enums.AnomalySeverity;
import cloud.chlora.shared.enums.AnomalyType;

import java.time.Instant;

public record SensorAnomalyDetectedEvent(
        String deviceId,
        Instant deviceTimestamp,
        Instant detectedAt,
        AnomalyType type,
        AnomalySeverity severity,
        String reason,
        float actualValue,
        float expectedMin,
        float expectedMax
) {}