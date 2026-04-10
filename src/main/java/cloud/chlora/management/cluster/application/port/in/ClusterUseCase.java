package cloud.chlora.management.cluster.application.port.in;

import cloud.chlora.management.cluster.adapter.in.web.request.ClusterCreateRequest;
import cloud.chlora.management.cluster.adapter.in.web.request.ClusterUpdateRequest;
import cloud.chlora.management.cluster.adapter.in.web.response.*;

public interface ClusterUseCase {

    PagedClusterResponse findAll(
            int page, int size, String search, String sort, String order
    );

    ClusterGetResponse findByClusterId(String clusterId);

    ClusterListResponse getClusterList();

    ClusterCreateResponse createCluster(ClusterCreateRequest request);

    ClusterUpdateResponse updateCluster(String clusterId, ClusterUpdateRequest request);

    void deleteCluster(String clusterId);
}