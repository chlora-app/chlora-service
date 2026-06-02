package cloud.chlora.report.telemetry.adapter.in.web.request;

import org.springframework.web.bind.annotation.RequestParam;

public record TelemetryReportFilterRequest(
        @RequestParam(defaultValue = "1")    int    page,
        @RequestParam(defaultValue = "10")   int    size,
        @RequestParam                        String dateFrom,
        @RequestParam                        String dateTo,
        @RequestParam(required = false)      String potId,
        @RequestParam(defaultValue = "desc") String order
) {}