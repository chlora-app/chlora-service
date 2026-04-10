package cloud.chlora.management.cluster.domain.port;

import cloud.chlora.management.cluster.adapter.in.web.request.ClusterCreateRequest;
import cloud.chlora.management.cluster.adapter.in.web.request.ClusterUpdateRequest;
import cloud.chlora.management.cluster.domain.model.Cluster;

public interface ClusterWriteRepository {

    Cluster create(ClusterCreateRequest request);

    Cluster update(String clusterId, ClusterUpdateRequest request);

    void softDelete(String clusterId);

    void softDeleteDevicesByClusterId(String clusterId);
}