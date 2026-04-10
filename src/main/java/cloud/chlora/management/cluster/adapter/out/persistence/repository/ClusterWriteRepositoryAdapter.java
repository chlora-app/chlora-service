package cloud.chlora.management.cluster.adapter.out.persistence.repository;

import cloud.chlora.management.cluster.adapter.in.web.request.ClusterCreateRequest;
import cloud.chlora.management.cluster.adapter.in.web.request.ClusterUpdateRequest;
import cloud.chlora.management.cluster.adapter.out.persistence.entity.ClusterWriteEntity;
import cloud.chlora.management.cluster.domain.model.Cluster;
import cloud.chlora.management.cluster.domain.port.ClusterWriteRepository;
import cloud.chlora.management.shared.error.IotErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class ClusterWriteRepositoryAdapter implements ClusterWriteRepository {

    private final ClusterWriteJpaRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Cluster create(ClusterCreateRequest request) {
        ClusterWriteEntity entity = ClusterWriteEntity.builder()
                .clusterName(request.clusterName())
                .build();

        return toDomain(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Cluster update(String clusterId, ClusterUpdateRequest request) {
        ClusterWriteEntity entity = repository.findByClusterId(clusterId)
                .orElseThrow(() -> AppException.of(IotErrorCode.CLUSTER_NOT_FOUND));

        entity.setClusterName(request.clusterName());

        return toDomain(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void softDelete(String clusterId) {
        repository.softDelete(clusterId, Instant.now());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void softDeleteDevicesByClusterId(String clusterId) {
        repository.softDeleteDevices(clusterId, Instant.now());
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private Cluster toDomain(ClusterWriteEntity e) {
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