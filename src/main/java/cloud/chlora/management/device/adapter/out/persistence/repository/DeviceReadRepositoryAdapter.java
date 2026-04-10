package cloud.chlora.management.device.adapter.out.persistence.repository;

import cloud.chlora.management.device.adapter.out.persistence.mapper.DevicePersistenceMapper;
import cloud.chlora.management.device.domain.model.Device;
import cloud.chlora.management.device.domain.model.DeviceStatus;
import cloud.chlora.management.device.domain.port.DeviceReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeviceReadRepositoryAdapter implements DeviceReadRepository {

    private final DeviceReadJpaRepository repository;

    @Override
    public Optional<Device> findByDeviceId(String deviceId) {
        return repository.findByDeviceId(deviceId)
                .map(DevicePersistenceMapper::toDomain);
    }

    @Override
    public List<Device> findAll(
            String search, String clusterId, DeviceStatus status,
            String sortColumn, String sortDirection,
            int limit, int offset
    ) {
        var pageable = buildPageable(limit, offset, sortColumn, sortDirection);
        return repository.findAllFiltered(
                        normalize(search),
                        normalize(clusterId),
                        status == null ? null : status.name(),
                        pageable
                )
                .map(DevicePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public long countAll(String search, String clusterId, DeviceStatus status) {
        return repository.findAllFiltered(
                        normalize(search),
                        normalize(clusterId),
                        status == null ? null : status.name(),
                        PageRequest.of(0, 1)
                )
                .getTotalElements();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private PageRequest buildPageable(int limit, int offset, String col, String dir) {
        var direction = "DESC".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(offset / limit, limit, Sort.by(direction, toColumnName(col)));
    }

    private String toColumnName(String col) {
        return switch (col) {
            case "deviceId"   -> "device_id";
            case "deviceName" -> "device_name";
            case "deviceType" -> "device_type";
            case "status"     -> "status";
            case "clusterId"  -> "cluster_id";
            default           -> "created_at";
        };
    }

    private String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}