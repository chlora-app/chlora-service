package cloud.chlora.pipeline.ingestion.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MqttPayloadParserTest {

    private MqttPayloadParser parser;

    @BeforeEach
    void setUp() {
        parser = new MqttPayloadParser();
    }

    @Test
    @DisplayName("Should parse valid JSON payload correctly")
    void should_parseValidJson_correctly() {
        // Arrange
        String topic = "chlora/telemetry/dev-001";
        String payload = """
                {
                    "device_id": "dev-001",
                    "timestamp": 1712812345678,
                    "soil_moisture": 45.5,
                    "temperature": 24.2,
                    "humidity": 60.1,
                    "battery_level": 88.0
                }
                """;
        Instant receivedAt = Instant.now();

        // Act
        MqttPayload result = parser.parse(topic, payload, receivedAt);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.metadata()).isNotNull();
        assertThat(result.metadata().topic()).isEqualTo(topic);
        assertThat(result.metadata().receivedAt()).isEqualTo(receivedAt);
        assertThat(result.metadata().messageId()).isNotBlank();

        assertThat(result.data()).isNotNull();
        assertThat(result.data().deviceId()).isEqualTo("dev-001");
        assertThat(result.data().timestamp()).isEqualTo(1712812345678L);
        assertThat(result.data().soilMoisture()).isEqualTo(45.5f);
        assertThat(result.data().temperature()).isEqualTo(24.2f);
        assertThat(result.data().humidity()).isEqualTo(60.1f);
        assertThat(result.data().batteryLevel()).isEqualTo(88.0f);
    }

    @Test
    @DisplayName("Should throw exception when payload is invalid JSON")
    void should_throwException_when_payloadIsInvalidJson() {
        // Arrange
        String topic = "chlora/telemetry/dev-001";
        String payload = "{ invalid json }";
        Instant receivedAt = Instant.now();

        // Act & Assert
        assertThatThrownBy(() -> parser.parse(topic, payload, receivedAt))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle missing optional fields if any (assuming null/default)")
    void should_handleMissingFields() {
        // Arrange
        String topic = "chlora/telemetry/dev-001";
        String payload = """
                {
                    "device_id": "dev-001",
                    "timestamp": 1712812345678
                }
                """;
        Instant receivedAt = Instant.now();

        // Act
        MqttPayload result = parser.parse(topic, payload, receivedAt);

        // Assert
        assertThat(result.data().deviceId()).isEqualTo("dev-001");
        assertThat(result.data().soilMoisture()).isEqualTo(0.0f); // Default for float
    }
}
