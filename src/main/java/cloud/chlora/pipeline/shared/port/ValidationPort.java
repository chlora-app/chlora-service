package cloud.chlora.pipeline.shared.port;

import cloud.chlora.pipeline.ingestion.internal.MqttPayload;
import cloud.chlora.pipeline.shared.ValidationResult;

public interface ValidationPort {

    ValidationResult validate(MqttPayload payload);
}
