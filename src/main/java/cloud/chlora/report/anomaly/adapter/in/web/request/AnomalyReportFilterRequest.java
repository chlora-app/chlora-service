package cloud.chlora.report.anomaly.adapter.in.web.request;

import org.springframework.web.bind.annotation.RequestParam;

public record AnomalyReportFilterRequest(
        @RequestParam(defaultValue = "1")    int    page,
        @RequestParam(defaultValue = "10")   int    size,
        @RequestParam                        String dateFrom,
        @RequestParam                        String dateTo,
        @RequestParam(required = false)      String potId,
        @RequestParam(required = false)      String anomalyType,
        @RequestParam(required = false)      String severity,
        @RequestParam(defaultValue = "desc") String order
) {}