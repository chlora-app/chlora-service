package cloud.chlora.pipeline.anomaly.application.usecase;

import cloud.chlora.pipeline.anomaly.domain.model.DetectionRequest;

public interface UpdateAnomalyUseCase {
    void update(DetectionRequest request, Long telemetryId);
}
