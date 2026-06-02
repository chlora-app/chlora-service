package cloud.chlora.report.anomaly.adapter.in.web;

import cloud.chlora.report.anomaly.adapter.in.web.request.AnomalyReportFilterRequest;
import cloud.chlora.report.anomaly.adapter.in.web.response.AnomalyReportResponse;
import cloud.chlora.report.anomaly.application.usecase.GetAnomalyReportUseCase;
import cloud.chlora.report.anomaly.domain.model.AnomalyReport;
import cloud.chlora.report.anomaly.domain.model.AnomalyReportQuery;
import cloud.chlora.report.shared.BaseReportResponse;
import cloud.chlora.report.shared.SortOrder;
import cloud.chlora.shared.enums.AnomalySeverity;
import cloud.chlora.shared.enums.AnomalyType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/reports/anomaly")
@RequiredArgsConstructor
public class AnomalyReportController {

    private static final ZoneId ZONE_JAKARTA = ZoneId.of("Asia/Jakarta");

    private final GetAnomalyReportUseCase getReportUseCase;
    private final AnomalyReportMapper     mapper;

    @GetMapping
    public ResponseEntity<@NonNull BaseReportResponse<AnomalyReportResponse>> getAnomalyReport(
            AnomalyReportFilterRequest filter
    ) {
        Instant dateFrom = LocalDate.parse(filter.dateFrom())
                .atStartOfDay(ZONE_JAKARTA)
                .toInstant();
        Instant dateTo   = LocalDate.parse(filter.dateTo())
                .plusDays(1)
                .atStartOfDay(ZONE_JAKARTA)
                .toInstant();

        AnomalyReportQuery query = AnomalyReportQuery.builder()
                .page(       filter.page())
                .size(       filter.size())
                .dateFrom(   dateFrom)
                .dateTo(     dateTo)
                .potId(      toNullIfBlank(filter.potId()))
                .anomalyType(toEnum(filter.anomalyType(), AnomalyType.class))
                .severity(   toEnum(filter.severity(),    AnomalySeverity.class))
                .order(      SortOrder.fromString(filter.order()))
                .build();

        BaseReportResponse<AnomalyReport> domainResult = getReportUseCase.getReport(query);

        BaseReportResponse<AnomalyReportResponse> response = new BaseReportResponse<>(
                domainResult.totalElements(),
                domainResult.page(),
                domainResult.size(),
                domainResult.totalPages(),
                domainResult.contents().stream().map(mapper::toResponse).toList()
        );

        return ResponseEntity.ok(response);
    }

    private String toNullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private <E extends Enum<E>> E toEnum(String value, Class<E> enumClass) {
        if (value == null || value.isBlank()) return null;
        return Enum.valueOf(enumClass, value.strip().toUpperCase());
    }
}