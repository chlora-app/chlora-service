package cloud.chlora.report.telemetry.domain.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TelemetryReport(
        String  potName,
        String  deviceName,
        float   soilMoisture,
        float   temperature,
        float   humidity,
        int     batteryLevel,
        Instant timestamp,
        long    latency
) {}