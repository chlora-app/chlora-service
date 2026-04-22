package cloud.chlora.pipeline.telemetry.adapter.out.persistence.repository;

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
    public void save(Telemetry telemetry) {
        jpaRepository.save(TelemetryPersistenceMapper.toEntity(telemetry));
    }
}