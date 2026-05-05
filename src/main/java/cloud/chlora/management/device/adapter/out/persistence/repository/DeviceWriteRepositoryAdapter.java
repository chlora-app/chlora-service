package cloud.chlora.management.device.adapter.out.persistence.repository;

import cloud.chlora.management.shared.error.IotErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import cloud.chlora.management.device.adapter.in.web.request.DeviceCreateRequest;
import cloud.chlora.management.device.adapter.in.web.request.DeviceUpdateRequest;
import cloud.chlora.management.device.adapter.out.persistence.entity.DeviceWriteEntity;
import cloud.chlora.management.device.domain.model.Device;
import cloud.chlora.management.device.domain.model.DeviceStatus;
import cloud.chlora.management.device.domain.port.DeviceWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class DeviceWriteRepositoryAdapter implements DeviceWriteRepository {

    private final DeviceWriteJpaRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Device create(DeviceCreateRequest request) {
        DeviceWriteEntity entity = DeviceWriteEntity.builder()
                .deviceName(request.deviceName())
                .potId(request.potId())
                .status(DeviceStatus.OFFLINE)
                .build();

        return toDomain(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Device update(String deviceId, DeviceUpdateRequest request) {
        DeviceWriteEntity entity = repository.findByDeviceId(deviceId)
                .orElseThrow(() -> AppException.of(IotErrorCode.DEVICE_NOT_FOUND));

        if (request.deviceName() != null) entity.setDeviceName(request.deviceName());
        if (request.potId()      != null) entity.setPotId(request.potId());
        if (request.status()     != null) {
            entity.setStatus(DeviceStatus.valueOf(request.status().trim().toUpperCase()));
        }

        return toDomain(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void softDelete(String deviceId) {
        int affected = repository.softDelete(deviceId, Instant.now());
        if (affected == 0) {
            throw AppException.of(IotErrorCode.DEVICE_NOT_FOUND);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int setOfflineIfStale(Instant threshold) {
        return repository.setOfflineIfStale(threshold);
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private Device toDomain(DeviceWriteEntity e) {
        return Device.builder()
                .deviceId(e.getDeviceId())
                .deviceName(e.getDeviceName())
                .status(e.getStatus())
                .potId(e.getPotId())
                .potName(null)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .deletedAt(e.getDeletedAt())
                .build();
    }
}