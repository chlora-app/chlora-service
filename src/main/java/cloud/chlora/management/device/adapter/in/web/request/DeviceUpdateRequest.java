package cloud.chlora.management.device.adapter.in.web.request;

public record DeviceUpdateRequest(
        String deviceName,
        String deviceType,
        String status,
        String clusterId
) {}