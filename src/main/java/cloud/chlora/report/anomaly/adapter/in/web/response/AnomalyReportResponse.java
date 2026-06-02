package cloud.chlora.report.anomaly.adapter.in.web.response;

import cloud.chlora.shared.enums.AnomalySeverity;
import cloud.chlora.shared.enums.AnomalyType;

import java.time.Instant;

public record AnomalyReportResponse(
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