package cloud.chlora.report.telemetry.adapter.in.web;

import cloud.chlora.report.telemetry.adapter.in.web.response.TelemetryReportResponse;
import cloud.chlora.report.telemetry.domain.model.TelemetryReport;
import org.springframework.stereotype.Component;

@Component
public class TelemetryReportMapper {

    public TelemetryReportResponse toResponse(TelemetryReport report) {
        return new TelemetryReportResponse(
                report.potName(),
                report.deviceName(),
                report.soilMoisture(),
                report.temperature(),
                report.humidity(),
                report.batteryLevel(),
                report.timestamp(),
                report.latency()
        );
    }
}