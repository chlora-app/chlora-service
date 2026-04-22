package cloud.chlora.management.device.adapter.in.web.response;

import cloud.chlora.management.device.domain.model.DeviceStatus;

public record DeviceListResponse(
        String deviceId,
        String deviceName,
        DeviceStatus status
) {}