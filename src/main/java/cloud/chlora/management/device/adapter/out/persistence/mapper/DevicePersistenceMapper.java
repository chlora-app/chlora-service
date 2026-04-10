package cloud.chlora.management.device.adapter.out.persistence.mapper;

import cloud.chlora.management.device.adapter.out.persistence.entity.DeviceReadEntity;
import cloud.chlora.management.device.domain.model.Device;

public final class DevicePersistenceMapper {

    private DevicePersistenceMapper() {}

    public static Device toDomain(DeviceReadEntity e) {
        return new Device(
                e.getId(),
                e.getDeviceId(),
                e.getDeviceName(),
                e.getDeviceType(),
                e.getStatus(),
                e.getClusterId(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt()
        );
    }
}