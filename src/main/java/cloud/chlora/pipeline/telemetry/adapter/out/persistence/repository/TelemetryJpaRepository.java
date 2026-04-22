package cloud.chlora.pipeline.telemetry.adapter.out.persistence.repository;

import cloud.chlora.pipeline.telemetry.adapter.out.persistence.entity.TelemetryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelemetryJpaRepository extends JpaRepository<TelemetryEntity, Long> {
}