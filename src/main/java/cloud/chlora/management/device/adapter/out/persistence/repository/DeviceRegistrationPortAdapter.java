package cloud.chlora.management.device.adapter.out.persistence.repository;

import cloud.chlora.management.device.adapter.out.persistence.entity.DeviceWriteEntity;
import cloud.chlora.management.device.domain.model.DeviceStatus;
import cloud.chlora.shared.port.DeviceRegistrationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DeviceRegistrationPortAdapter implements DeviceRegistrationPort {

    private final DeviceRegistrationJpaRepository jpaRepository;

    @Override
    public boolean isDeviceRegistered(String deviceId) {
        return jpaRepository.existsByDeviceId(deviceId);
    }

    @Override
    public void registerDevice(String deviceId) {
        var entity = DeviceWriteEntity.builder()
                .deviceId(deviceId)
                .deviceName(deviceId)
                .status(DeviceStatus.OFFLINE)
                .potId("UNASSIGNED")
                .build();

        jpaRepository.save(entity);
        log.info("[Device] Auto-registered device: {}", deviceId);
    }

    @Override
    public void setDeviceOnline(String deviceId) {
        try {
            jpaRepository.setDeviceOnline(deviceId);
        } catch (Exception e) {
            log.error("[Device] Failed to set device {} online", deviceId, e);
        }

        log.info("[Device] Device {} set online", deviceId);
    }
}