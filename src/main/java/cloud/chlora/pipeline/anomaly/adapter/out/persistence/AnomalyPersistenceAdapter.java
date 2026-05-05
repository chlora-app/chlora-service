package cloud.chlora.pipeline.anomaly.adapter.out.persistence;

import cloud.chlora.pipeline.anomaly.domain.model.Anomaly;
import cloud.chlora.pipeline.anomaly.domain.port.AnomalyPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnomalyPersistenceAdapter implements AnomalyPersistencePort {

    private final AnomalyJpaRepository repository;

    @Override
    public void save(Anomaly anomaly) {
        AnomalyEntity anomalyEntity = toEntity(anomaly);
        repository.save(anomalyEntity);
    }

    private AnomalyEntity toEntity(Anomaly anomaly) {
        return AnomalyEntity.builder()
                .anomalyType(anomaly.anomalyType())
                .severity(anomaly.severity())
                .anomalyScore(anomaly.anomalyScore())
                .detectedBy(anomaly.detectedBy())
                .modelVersion(anomaly.modelVersion())
                .detectedAt(anomaly.detectedAt())
                .telemetryId(anomaly.telemetryId())
                .notificationId(anomaly.notificationId())
                .build();
    }
}
