package cloud.chlora.management.device.adapter.out.persistence.repository;

import cloud.chlora.management.device.adapter.out.persistence.mapper.DevicePersistenceMapper;
import cloud.chlora.management.device.domain.model.Device;
import cloud.chlora.management.device.domain.model.DeviceStatus;
import cloud.chlora.management.device.domain.port.DeviceReadRepository;
import lombok.RequiredArgsConstructor;
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
            String search, String potId, DeviceStatus status,
            String sortColumn, String sortDirection,
            int limit, int offset
    ) {
        return repository.findAllFiltered(
                        normalize(search),
                        normalize(potId),
                        status == null ? null : status.name(),
                        toColumnName(sortColumn),
                        sortDirection.toUpperCase(),
                        limit,
                        offset
                )
                .stream()
                .map(DevicePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public long countAll(String search, String potId, DeviceStatus status) {
        return repository.countAllFiltered(
                normalize(search),
                normalize(potId),
                status == null ? null : status.name()
        );
    }

    @Override
    public Optional<Device> findByPotId(String potId) {
        return repository.findByPotIdAndDeletedAtIsNull(potId)
                .map(DevicePersistenceMapper::toDomain);
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private String toColumnName(String col) {
        return switch (col) {
            case "deviceId"   -> "device_id";
            case "deviceName" -> "device_name";
            case "status"     -> "status";
            case "potId"      -> "pot_id";
            case "potName"    -> "pot_name";
            default           -> "created_at";
        };
    }

    private String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}