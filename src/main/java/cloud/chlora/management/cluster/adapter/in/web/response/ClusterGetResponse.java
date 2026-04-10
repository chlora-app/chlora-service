package cloud.chlora.management.cluster.adapter.in.web.response;

import cloud.chlora.management.device.adapter.in.web.response.DeviceListResponse;

import java.util.List;

public record ClusterGetResponse(
        String clusterId,
        String clusterName,
        long totalDevices,
        List<DeviceListResponse> devices
) {}