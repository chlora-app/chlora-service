package cloud.chlora.report.anomaly.adapter.in.web;

import cloud.chlora.report.anomaly.adapter.in.web.response.AnomalyReportResponse;
import cloud.chlora.report.anomaly.domain.model.AnomalyReport;
import org.springframework.stereotype.Component;

@Component
public class AnomalyReportMapper {

    public AnomalyReportResponse toResponse(AnomalyReport report) {
        return new AnomalyReportResponse(
                report.potName(),
                report.deviceName(),
                report.soilMoisture(),
                report.temperature(),
                report.humidity(),
                report.batteryLevel(),
                report.timestamp(),
                report.latency(),
                report.anomalyType(),
                report.severity(),
                report.anomalyScore()
        );
    }
}