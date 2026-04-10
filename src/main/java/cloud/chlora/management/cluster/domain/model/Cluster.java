package cloud.chlora.management.cluster.domain.model;

import java.time.Instant;

public record Cluster(
        Long id,
        String clusterId,
        String clusterName,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
    public boolean isDeleted() {
        return deletedAt != null;
    }
}