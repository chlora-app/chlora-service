package cloud.chlora.pipeline.ingestion.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record MqttPayload(
        MqttMetadata metadata,
        MqttData data
) {
    public record MqttMetadata(String messageId, String topic, Instant receivedAt) {}

    public record MqttData(
            @JsonProperty("device_id")
            String deviceId,

            @JsonProperty("timestamp")
            long timestamp,

            @JsonProperty("soil_moisture")
            float soilMoisture,

            @JsonProperty("temperature")
            float temperature,

            @JsonProperty("humidity")
            float humidity,

            @JsonProperty("battery_level")
            float batteryLevel
    ) {}
}