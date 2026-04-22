package cloud.chlora.management.device.adapter.in.web.response;

import cloud.chlora.management.device.domain.model.DeviceStatus;

import java.time.Instant;

public record DeviceUpdateResponse(
        String deviceId,
        String deviceName,
        DeviceStatus status,
        String potId,
        Instant updatedAt
) {}