package cloud.chlora.report.shared;

import java.util.List;

public record BaseReportResponse<T>(
        int totalElements,
        int page,
        int size,
        int totalPages,
        List<T> contents
) {}
