package cloud.chlora.pipeline.anomaly.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DetectionRequest(

        @JsonProperty("device_id")
        String deviceId,

        @JsonProperty("soil_moisture")
        float soilMoisture,

        @JsonProperty("temperature")
        float temperature,

        @JsonProperty("humidity")
        float humidity
) {}
