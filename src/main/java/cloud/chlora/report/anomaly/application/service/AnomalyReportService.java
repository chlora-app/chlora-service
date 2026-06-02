package cloud.chlora.report.anomaly.application.service;

import cloud.chlora.report.anomaly.application.usecase.GetAnomalyReportUseCase;
import cloud.chlora.report.anomaly.domain.model.AnomalyReport;
import cloud.chlora.report.anomaly.domain.model.AnomalyReportQuery;
import cloud.chlora.report.anomaly.domain.port.AnomalyReportRepository;
import cloud.chlora.report.shared.BaseReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnomalyReportService implements GetAnomalyReportUseCase {

    private final AnomalyReportRepository reportRepository;

    @Override
    @Transactional(readOnly = true)
    public BaseReportResponse<AnomalyReport> getReport(AnomalyReportQuery query) {
        List<AnomalyReport> contents   = reportRepository.findAll(query);
        long                total      = reportRepository.countAll(query);
        int                 totalPages = (int) Math.ceil((double) total / query.size());

        return new BaseReportResponse<>(
                (int) total,
                query.page(),
                query.size(),
                totalPages,
                contents
        );
    }
}