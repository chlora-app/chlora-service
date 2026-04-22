package cloud.chlora.management.device.domain.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record Device(
        Long id,
        String deviceId,
        String deviceName,
        DeviceStatus status,
        String potId,
        String potName,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
    public boolean isDeleted() {
        return deletedAt != null;
    }
}