package cloud.chlora.pipeline.ingestion.internal;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Component
public class MqttPayloadParser {

    private final ObjectMapper mapper = new ObjectMapper();

    public MqttPayload parse(String topic, String payload, Instant receivedAt) {
        var metadata = new MqttPayload.MqttMetadata(
                UUID.randomUUID().toString(),
                topic,
                receivedAt
        );

        var data = mapper.readValue(payload, MqttPayload.MqttData.class);

        return new MqttPayload(metadata, data);
    }
}
