package cloud.chlora.management.cluster.adapter.in.web.response;

import java.time.Instant;

public record ClusterCreateResponse(
        String clusterId,
        String clusterName,
        Instant createdAt
) {}