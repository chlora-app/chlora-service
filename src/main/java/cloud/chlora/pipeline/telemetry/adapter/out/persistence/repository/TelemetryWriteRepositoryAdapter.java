package cloud.chlora.pipeline.telemetry.adapter.out.persistence.repository;

import cloud.chlora.pipeline.telemetry.adapter.out.persistence.entity.TelemetryEntity;
import cloud.chlora.pipeline.telemetry.adapter.out.persistence.mapper.TelemetryPersistenceMapper;
import cloud.chlora.pipeline.telemetry.domain.model.Telemetry;
import cloud.chlora.pipeline.telemetry.domain.port.TelemetryWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TelemetryWriteRepositoryAdapter implements TelemetryWriteRepository {

    private final TelemetryJpaRepository jpaRepository;

    @Override
    public Telemetry save(Telemetry telemetry) {
        TelemetryEntity entity = jpaRepository.save(TelemetryPersistenceMapper.toEntity(telemetry));
        return TelemetryPersistenceMapper.toDomain(entity);
    }
}