package cloud.chlora.report.telemetry.application.usecase;

import cloud.chlora.report.shared.BaseReportResponse;
import cloud.chlora.report.telemetry.domain.model.TelemetryReport;
import cloud.chlora.report.telemetry.domain.model.TelemetryReportQuery;

public interface GetTelemetryReportUseCase {
    BaseReportResponse<TelemetryReport> getReport(TelemetryReportQuery query);
}
