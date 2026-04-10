package cloud.chlora.management.cluster.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;

public record ClusterCreateRequest(

        @NotBlank
        String clusterName
) {}