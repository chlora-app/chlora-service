package cloud.chlora.management.device.adapter.in.web.response;

import cloud.chlora.management.device.domain.model.DeviceStatus;

import java.time.Instant;

public record DeviceGetResponse(
        String deviceId,
        String deviceName,
        String deviceType,
        String clusterId,
        DeviceStatus status,
        Instant createdAt,
        Instant updatedAt
) {}