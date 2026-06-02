package cloud.chlora.report.anomaly.application.usecase;

import cloud.chlora.report.anomaly.domain.model.AnomalyReport;
import cloud.chlora.report.anomaly.domain.model.AnomalyReportQuery;
import cloud.chlora.report.shared.BaseReportResponse;

public interface GetAnomalyReportUseCase {
    BaseReportResponse<AnomalyReport> getReport(AnomalyReportQuery query);
}