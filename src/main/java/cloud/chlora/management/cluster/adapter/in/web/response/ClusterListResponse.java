package cloud.chlora.management.cluster.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ClusterListResponse(List<ClusterInfo> list) {

    public record ClusterInfo(
            @JsonProperty("label") String clusterName,
            @JsonProperty("value") String clusterId
    ) {}
}