package cloud.chlora.report.telemetry.adapter.in.web;

import cloud.chlora.report.shared.BaseReportResponse;
import cloud.chlora.report.shared.SortOrder;
import cloud.chlora.report.telemetry.adapter.in.web.request.TelemetryReportFilterRequest;
import cloud.chlora.report.telemetry.adapter.in.web.response.TelemetryReportResponse;
import cloud.chlora.report.telemetry.application.usecase.GetTelemetryReportUseCase;
import cloud.chlora.report.telemetry.domain.model.TelemetryReport;
import cloud.chlora.report.telemetry.domain.model.TelemetryReportQuery;
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
@RequestMapping("/api/reports/telemetry")
@RequiredArgsConstructor
public class TelemetryReportController {

    private static final ZoneId ZONE_JAKARTA = ZoneId.of("Asia/Jakarta");

    private final GetTelemetryReportUseCase getReportUseCase;
    private final TelemetryReportMapper     mapper;

    @GetMapping
    public ResponseEntity<@NonNull BaseReportResponse<TelemetryReportResponse>> getTelemetryReport(
            TelemetryReportFilterRequest filter
    ) {
        Instant dateFrom = LocalDate.parse(filter.dateFrom())
                .atStartOfDay(ZONE_JAKARTA)
                .toInstant();
        Instant dateTo   = LocalDate.parse(filter.dateTo())
                .plusDays(1)
                .atStartOfDay(ZONE_JAKARTA)
                .toInstant();

        TelemetryReportQuery query = TelemetryReportQuery.builder()
                .page(    filter.page())
                .size(    filter.size())
                .dateFrom(dateFrom)
                .dateTo(  dateTo)
                .potId(   toNullIfBlank(filter.potId()))
                .order(   SortOrder.fromString(filter.order()))
                .build();

        BaseReportResponse<TelemetryReport> domainResult = getReportUseCase.getReport(query);

        BaseReportResponse<TelemetryReportResponse> response = new BaseReportResponse<>(
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
}