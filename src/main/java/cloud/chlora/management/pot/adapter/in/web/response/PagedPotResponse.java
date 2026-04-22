package cloud.chlora.management.pot.adapter.in.web.response;

import java.util.List;

public record PagedPotResponse(
        long totalElements,
        int page,
        int size,
        int totalPages,
        List<PotSummaryResponse> pots
) {}