package cloud.chlora.management.cluster.adapter.out.persistence.repository;

import cloud.chlora.management.cluster.adapter.out.persistence.mapper.ClusterPersistenceMapper;
import cloud.chlora.management.cluster.domain.model.Cluster;
import cloud.chlora.management.cluster.domain.port.ClusterReadRepository;
import cloud.chlora.management.device.adapter.out.persistence.mapper.DevicePersistenceMapper;
import cloud.chlora.management.device.adapter.out.persistence.repository.DeviceReadJpaRepository;
import cloud.chlora.management.device.domain.model.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClusterReadRepositoryAdapter implements ClusterReadRepository {

    private final ClusterReadJpaRepository clusterRepo;
    private final DeviceReadJpaRepository  deviceRepo;

    @Override
    public Optional<Cluster> findByClusterId(String clusterId) {
        return clusterRepo.findByClusterId(clusterId)
                .map(ClusterPersistenceMapper::toDomain);
    }

    @Override
    public List<Cluster> findAllExisting(
            String search,
            String sortColumn, String sortDirection,
            int limit, int offset
    ) {
        var pageable = buildPageable(limit, offset, sortColumn, sortDirection);
        return clusterRepo.findAllExisting(normalize(search), pageable)
                .map(ClusterPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public long countExisting(String search) {
        return clusterRepo.findAllExisting(normalize(search), PageRequest.of(0, 1))
                .getTotalElements();
    }

    @Override
    public List<Cluster> findAllAsList() {
        return clusterRepo.findAllAsList()
                .stream()
                .map(ClusterPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public long countDevices(String clusterId) {
        return clusterRepo.countDevicesByClusterId(clusterId);
    }

    @Override
    public List<Device> findDevicesByClusterId(String clusterId) {
        return deviceRepo.findAllByClusterIdActive(clusterId)
                .stream()
                .map(DevicePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByClusterName(String clusterName) {
        return clusterRepo.existsByClusterNameActive(clusterName);
    }

    @Override
    public boolean existsByClusterId(String clusterId) {
        return clusterRepo.existsByClusterIdActive(clusterId);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private PageRequest buildPageable(int limit, int offset, String col, String dir) {
        var direction = "DESC".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(offset / limit, limit, Sort.by(direction, toColumnName(col)));
    }

    private String toColumnName(String col) {
        return switch (col) {
            case "clusterId"   -> "cluster_id";
            case "clusterName" -> "cluster_name";
            case "createdAt"   -> "created_at";
            default            -> "created_at";
        };
    }

    private String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}