package cloud.chlora.report.telemetry.domain.port;

import cloud.chlora.report.telemetry.domain.model.TelemetryReport;
import cloud.chlora.report.telemetry.domain.model.TelemetryReportQuery;

import java.util.List;

public interface TelemetryReportRepository {
    List<TelemetryReport> findAll(TelemetryReportQuery query);
    long countAll(TelemetryReportQuery query);
}