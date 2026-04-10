package cloud.chlora.management.device.adapter.in.web.response;

import cloud.chlora.management.device.domain.model.DeviceStatus;

import java.time.Instant;

public record PagedDeviceItem(
        String deviceId,
        String deviceName,
        String deviceType,
        DeviceStatus status,
        String clusterId,
        String clusterName,
        Instant createdAt
) {}