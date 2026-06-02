package cloud.chlora.management.device.adapter.in.web.response;

import cloud.chlora.management.device.domain.model.DeviceStatus;

import java.time.Instant;

public record DeviceCreateResponse(
        String deviceId,
        String deviceName,
        String potId,
        DeviceStatus status,
        Instant createdAt
) {}