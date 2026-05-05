package cloud.chlora.pipeline.anomaly.domain.port;

import cloud.chlora.pipeline.anomaly.domain.model.Anomaly;

public interface AnomalyPersistencePort {
    void save(Anomaly anomaly);
}
