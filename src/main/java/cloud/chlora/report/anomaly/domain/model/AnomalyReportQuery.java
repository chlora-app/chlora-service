package cloud.chlora.report.anomaly.domain.model;

import cloud.chlora.report.shared.SortOrder;
import cloud.chlora.shared.enums.AnomalySeverity;
import cloud.chlora.shared.enums.AnomalyType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record AnomalyReportQuery(
        int             page,
        int             size,
        Instant         dateFrom,
        Instant         dateTo,
        String          potId,
        AnomalyType anomalyType,
        AnomalySeverity severity,
        SortOrder       order
) {}