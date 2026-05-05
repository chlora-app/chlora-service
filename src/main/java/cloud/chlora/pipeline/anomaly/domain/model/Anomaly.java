package cloud.chlora.pipeline.anomaly.domain.model;

import cloud.chlora.pipeline.shared.event.AnomalySeverity;
import cloud.chlora.pipeline.shared.event.AnomalyType;
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
