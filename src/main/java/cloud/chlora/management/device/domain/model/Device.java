package cloud.chlora.management.device.domain.model;

import java.time.Instant;

public record Device(
        Long id,
        String deviceId,
        String deviceName,
        String deviceType,
        DeviceStatus status,
        String clusterId,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
    public boolean isDeleted() {
        return deletedAt != null;
    }
}