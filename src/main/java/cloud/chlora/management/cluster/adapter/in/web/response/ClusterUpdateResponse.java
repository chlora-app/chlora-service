package cloud.chlora.management.cluster.adapter.in.web.response;

import java.time.Instant;

public record ClusterUpdateResponse(
        String clusterId,
        String clusterName,
        Instant updatedAt
) {}