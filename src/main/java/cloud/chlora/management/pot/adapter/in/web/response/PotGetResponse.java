package cloud.chlora.management.pot.adapter.in.web.response;

import cloud.chlora.management.device.adapter.in.web.response.DeviceListResponse;

import java.util.List;

public record PotGetResponse(
        String potId,
        String potName,
        long totalDevices,
        List<DeviceListResponse> devices
) {}