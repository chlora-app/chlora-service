package cloud.chlora.management.device.domain.port;

public interface ClusterExistencePort {

    boolean existsByClusterId(String clusterId);
}