package cloud.chlora.pipeline.anomaly.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DetectionResponse(
        @JsonProperty("is_anomaly")
        boolean isAnomaly,

        @JsonProperty("anomaly_score")
        float anomalyScore,

        @JsonProperty("anomaly_type")
        String anomalyType,

        @JsonProperty("severity")
        String severity,

        @JsonProperty("detected_by")
        String detectedBy,

        @JsonProperty("model_version")
        String modelVersion
) {}
