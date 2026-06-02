package cloud.chlora.report.telemetry.adapter.in.web.response;

import java.time.Instant;

public record TelemetryReportResponse(
        String  potName,
        String  deviceName,
        float   soilMoisture,
        float   temperature,
        float   humidity,
        int     batteryLevel,
        Instant timestamp,
        long    latency
) {}