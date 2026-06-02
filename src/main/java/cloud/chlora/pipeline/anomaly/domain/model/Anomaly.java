package cloud.chlora.pipeline.anomaly.domain.model;

import cloud.chlora.shared.enums.AnomalySeverity;
import cloud.chlora.shared.enums.AnomalyType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record Anomaly(
        Long id,
        AnomalyType anomalyType,
        AnomalySeverity severity,
        float anomalyScore,
        String detectedBy,
        String modelVersion,
        Instant detectedAt,
        Long telemetryId,
        String notificationId
) {}
