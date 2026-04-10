package cloud.chlora.management.cluster.adapter.out.persistence.mapper;

import cloud.chlora.management.cluster.adapter.out.persistence.entity.ClusterReadEntity;
import cloud.chlora.management.cluster.domain.model.Cluster;

public final class ClusterPersistenceMapper {

    private ClusterPersistenceMapper() {}

    public static Cluster toDomain(ClusterReadEntity e) {
        return new Cluster(
                e.getId(),
                e.getClusterId(),
                e.getClusterName(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt()
        );
    }
}