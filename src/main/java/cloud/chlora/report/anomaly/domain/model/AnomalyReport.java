package cloud.chlora.report.anomaly.domain.model;

import cloud.chlora.shared.enums.AnomalySeverity;
import cloud.chlora.shared.enums.AnomalyType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record AnomalyReport(
        String          potName,
        String          deviceName,
        float           soilMoisture,
        float           temperature,
        float           humidity,
        int             batteryLevel,
        Instant         timestamp,
        long            latency,
        AnomalyType     anomalyType,
        AnomalySeverity severity,
        float           anomalyScore
) {}