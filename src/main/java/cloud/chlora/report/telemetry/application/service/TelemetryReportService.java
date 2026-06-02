package cloud.chlora.report.telemetry.application.service;

import cloud.chlora.report.shared.BaseReportResponse;
import cloud.chlora.report.telemetry.application.usecase.GetTelemetryReportUseCase;
import cloud.chlora.report.telemetry.domain.model.TelemetryReport;
import cloud.chlora.report.telemetry.domain.model.TelemetryReportQuery;
import cloud.chlora.report.telemetry.domain.port.TelemetryReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryReportService implements GetTelemetryReportUseCase {

    private final TelemetryReportRepository reportRepository;

    @Override
    @Transactional(readOnly = true)
    public BaseReportResponse<TelemetryReport> getReport(TelemetryReportQuery query) {
        List<TelemetryReport> contents   = reportRepository.findAll(query);
        long total      = reportRepository.countAll(query);
        int totalPages = (int) Math.ceil((double) total / query.size());

        log.info("total count: {}, contents size: {}", total, contents.size());

        return new BaseReportResponse<>(
                (int) total,
                query.page(),
                query.size(),
                totalPages,
                contents
        );
    }
}