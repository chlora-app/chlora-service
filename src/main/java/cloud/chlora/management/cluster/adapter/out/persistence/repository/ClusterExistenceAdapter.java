package cloud.chlora.management.cluster.adapter.out.persistence.repository;

import cloud.chlora.management.device.domain.port.ClusterExistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ClusterExistenceAdapter implements ClusterExistencePort {

    private final ClusterReadJpaRepository repository;

    @Override
    public boolean existsByClusterId(String clusterId) {
        return repository.existsByClusterIdActive(clusterId);
    }
}