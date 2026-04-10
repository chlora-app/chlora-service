package cloud.chlora.management.cluster.adapter.in.web.response;

import java.util.List;

public record PagedClusterResponse(
        long totalElements,
        int page,
        int size,
        int totalPages,
        List<ClusterSummaryResponse> clusters
) {}