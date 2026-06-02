package cloud.chlora.report.telemetry.domain.model;

import cloud.chlora.report.shared.SortOrder;
import lombok.Builder;

import java.time.Instant;

@Builder
public record TelemetryReportQuery(
        int       page,
        int       size,
        Instant   dateFrom,
        Instant   dateTo,
        String    potId,
        SortOrder order
) {}