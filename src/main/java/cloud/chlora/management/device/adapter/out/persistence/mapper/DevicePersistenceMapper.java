package cloud.chlora.management.device.adapter.out.persistence.mapper;

import cloud.chlora.management.device.adapter.out.persistence.entity.DeviceReadEntity;
import cloud.chlora.management.device.adapter.out.persistence.repository.DeviceReadJpaRepository.DeviceWithPotProjection;
import cloud.chlora.management.device.domain.model.Device;
import cloud.chlora.management.device.domain.model.DeviceStatus;

public final class DevicePersistenceMapper {

    private DevicePersistenceMapper() {}

    public static Device toDomain(DeviceReadEntity e) {
        return Device.builder()
                .id(e.getId())
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

    public static Device toDomain(DeviceWithPotProjection p) {
        String rawPotId = p.getPotId();
        String potName  = p.getPotName();
        boolean assigned = rawPotId != null && !rawPotId.equals("UNASSIGNED") && potName != null;

        return Device.builder()
                .deviceId(p.getDeviceId())
                .deviceName(p.getDeviceName())
                .status(DeviceStatus.valueOf(p.getStatus()))
                .potId(assigned ? rawPotId : null)
                .potName(assigned ? potName : null)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(p.getDeletedAt())
                .build();
    }
}