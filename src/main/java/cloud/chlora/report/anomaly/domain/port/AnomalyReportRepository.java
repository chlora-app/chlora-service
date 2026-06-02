package cloud.chlora.report.anomaly.domain.port;

import cloud.chlora.report.anomaly.domain.model.AnomalyReport;
import cloud.chlora.report.anomaly.domain.model.AnomalyReportQuery;

import java.util.List;

public interface AnomalyReportRepository {
    List<AnomalyReport> findAll(AnomalyReportQuery query);
    long countAll(AnomalyReportQuery query);
}