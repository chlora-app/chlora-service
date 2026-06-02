package cloud.chlora.pipeline.anomaly.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnomalyJpaRepository extends JpaRepository<AnomalyEntity, Long> {
}
