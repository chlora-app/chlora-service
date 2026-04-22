package cloud.chlora.pipeline.ingestion.application.usecase;

import cloud.chlora.pipeline.shared.port.ValidationPort;
import cloud.chlora.pipeline.ingestion.internal.MqttPayload;
import cloud.chlora.pipeline.ingestion.internal.MqttPayloadParser;
import cloud.chlora.pipeline.shared.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryIngestUseCase {

    private final ValidationPort validationPort;
    private final MqttPayloadParser parser;

    public void handle(String topic, String rawPayload, Instant receivedAt) {
        MqttPayload payload = parser.parse(topic, rawPayload, receivedAt);
        log.info("[Ingestion] Received message: {}", payload.toString());

        ValidationResult result = validationPort.validate(payload);
        if (result.isInvalid()) {
            log.warn("[Ingestion] Invalid message: {}", result.reason());
        }
    }
}
