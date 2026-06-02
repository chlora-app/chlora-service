package cloud.chlora.pipeline.anomaly.domain.port;

import cloud.chlora.pipeline.anomaly.domain.model.DetectionRequest;
import cloud.chlora.pipeline.anomaly.domain.model.DetectionResponse;

public interface AnomalyDetectionPort {
    DetectionResponse detect(DetectionRequest request);
}
