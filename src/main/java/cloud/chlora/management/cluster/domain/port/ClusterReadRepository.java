package cloud.chlora.management.cluster.domain.port;

import cloud.chlora.management.cluster.domain.model.Cluster;
import cloud.chlora.management.device.domain.model.Device;

import java.util.List;
import java.util.Optional;

public interface ClusterReadRepository {

    Optional<Cluster> findByClusterId(String clusterId);

    List<Cluster> findAllExisting(
            String search,
            String sortColumn, String sortDirection,
            int limit, int offset
    );

    long countExisting(String search);

    List<Cluster> findAllAsList();

    long countDevices(String clusterId);

    List<Device> findDevicesByClusterId(String clusterId);

    boolean existsByClusterName(String clusterName);

    boolean existsByClusterId(String clusterId);
}