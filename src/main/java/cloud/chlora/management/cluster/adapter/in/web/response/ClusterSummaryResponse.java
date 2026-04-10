package cloud.chlora.management.cluster.adapter.in.web.response;

public record ClusterSummaryResponse(
        String clusterId,
        String clusterName,
        long totalDevices
) {}