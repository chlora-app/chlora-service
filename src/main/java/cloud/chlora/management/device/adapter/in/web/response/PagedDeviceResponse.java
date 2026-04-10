package cloud.chlora.management.device.adapter.in.web.response;

import java.util.List;

public record PagedDeviceResponse(
        long totalElements,
        int page,
        int size,
        int totalPages,
        List<PagedDeviceItem> devices
) {}