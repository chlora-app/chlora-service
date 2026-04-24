package cloud.chlora.pipeline.validation.application.validator;

import cloud.chlora.pipeline.ingestion.internal.MqttPayload;
import cloud.chlora.pipeline.shared.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TelemetryValidatorTest {

    private TelemetryValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TelemetryValidator();
    }

    @Test
    @DisplayName("Should return OK when all telemetry data is valid")
    void should_returnOk_when_allDataIsValid() {
        // Arrange
        MqttPayload payload = createValidPayload();

        // Act
        ValidationResult result = validator.validate(payload);

        // Assert
        assertThat(result.valid()).isTrue();
        assertThat(result.reason()).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Should fail when messageId is blank")
    void should_fail_when_messageIdIsBlank(String blankId) {
        MqttPayload.MqttMetadata metadata = new MqttPayload.MqttMetadata(blankId, "topic/test", Instant.now());
        MqttPayload payload = new MqttPayload(metadata, createValidData());

        ValidationResult result = validator.validate(payload);

        assertThat(result.valid()).isFalse();
        assertThat(result.reason()).contains("messageId is blank");
    }

    @Test
    @DisplayName("Should fail when receivedAt is null")
    void should_fail_when_receivedAtIsNull() {
        MqttPayload.MqttMetadata metadata = new MqttPayload.MqttMetadata("msg-123", "topic/test", null);
        MqttPayload payload = new MqttPayload(metadata, createValidData());

        ValidationResult result = validator.validate(payload);

        assertThat(result.valid()).isFalse();
        assertThat(result.reason()).contains("receivedAt is null");
    }

    @Test
    @DisplayName("Should fail when deviceId is blank")
    void should_fail_when_deviceIdIsBlank() {
        MqttPayload.MqttData data = new MqttPayload.MqttData("", System.currentTimeMillis(), 50.0f, 25.0f, 60.0f, 80.0f);
        MqttPayload payload = new MqttPayload(createValidMetadata(), data);

        ValidationResult result = validator.validate(payload);

        assertThat(result.valid()).isFalse();
        assertThat(result.reason()).contains("deviceId is blank");
    }

    @Test
    @DisplayName("Should fail when timestamp is zero or negative")
    void should_fail_when_timestampIsInvalid() {
        MqttPayload.MqttData data = new MqttPayload.MqttData("dev-01", 0, 50.0f, 25.0f, 60.0f, 80.0f);
        MqttPayload payload = new MqttPayload(createValidMetadata(), data);

        ValidationResult result = validator.validate(payload);

        assertThat(result.valid()).isFalse();
        assertThat(result.reason()).contains("timestamp must be positive");
    }

    @Test
    @DisplayName("Should fail when timestamp drift is too large")
    void should_fail_when_timestampDriftIsTooLarge() {
        // 600 seconds ago (max is 300)
        long oldTimestamp = Instant.now().minusSeconds(600).toEpochMilli();
        MqttPayload.MqttData data = new MqttPayload.MqttData("dev-01", oldTimestamp, 50.0f, 25.0f, 60.0f, 80.0f);
        MqttPayload payload = new MqttPayload(createValidMetadata(), data);

        ValidationResult result = validator.validate(payload);

        assertThat(result.valid()).isFalse();
        assertThat(result.reason()).contains("timestamp drift too large");
    }

    @ParameterizedTest
    @ValueSource(floats = {-1.0f, 101.0f})
    @DisplayName("Should fail when soilMoisture is out of range")
    void should_fail_when_soilMoistureIsOutOfRange(float value) {
        MqttPayload.MqttData data = createDataWithSoilMoisture(value);
        MqttPayload payload = new MqttPayload(createValidMetadata(), data);

        ValidationResult result = validator.validate(payload);

        assertThat(result.valid()).isFalse();
        assertThat(result.reason()).contains("soilMoisture out of range");
    }

    @ParameterizedTest
    @ValueSource(floats = {-41.0f, 86.0f})
    @DisplayName("Should fail when temperature is out of range")
    void should_fail_when_temperatureIsOutOfRange(float value) {
        MqttPayload.MqttData data = createDataWithTemperature(value);
        MqttPayload payload = new MqttPayload(createValidMetadata(), data);

        ValidationResult result = validator.validate(payload);

        assertThat(result.valid()).isFalse();
        assertThat(result.reason()).contains("temperature out of range");
    }

    @ParameterizedTest
    @ValueSource(floats = {-1.0f, 101.0f})
    @DisplayName("Should fail when humidity is out of range")
    void should_fail_when_humidityIsOutOfRange(float value) {
        MqttPayload.MqttData data = createDataWithHumidity(value);
        MqttPayload payload = new MqttPayload(createValidMetadata(), data);

        ValidationResult result = validator.validate(payload);

        assertThat(result.valid()).isFalse();
        assertThat(result.reason()).contains("humidity out of range");
    }

    @ParameterizedTest
    @ValueSource(floats = {-1.0f, 101.0f})
    @DisplayName("Should fail when batteryLevel is out of range")
    void should_fail_when_batteryLevelIsOutOfRange(float value) {
        MqttPayload.MqttData data = createDataWithBatteryLevel(value);
        MqttPayload payload = new MqttPayload(createValidMetadata(), data);

        ValidationResult result = validator.validate(payload);

        assertThat(result.valid()).isFalse();
        assertThat(result.reason()).contains("batteryLevel out of range");
    }

    @Test
    @DisplayName("Should collect multiple violations")
    void should_collectMultipleViolations() {
        MqttPayload.MqttData data = new MqttPayload.MqttData("", System.currentTimeMillis(), 150.0f, 200.0f, -10.0f, 120.0f);
        MqttPayload payload = new MqttPayload(createValidMetadata(), data);

        ValidationResult result = validator.validate(payload);

        assertThat(result.valid()).isFalse();
        String reason = result.reason();
        assertThat(reason).contains("deviceId is blank");
        assertThat(reason).contains("soilMoisture out of range");
        assertThat(reason).contains("temperature out of range");
        assertThat(reason).contains("humidity out of range");
        assertThat(reason).contains("batteryLevel out of range");
    }

    // ── Helper Methods ────────────────────────────────────────

    private MqttPayload createValidPayload() {
        return new MqttPayload(createValidMetadata(), createValidData());
    }

    private MqttPayload.MqttMetadata createValidMetadata() {
        return new MqttPayload.MqttMetadata("msg-123", "sensor/telemetry", Instant.now());
    }

    private MqttPayload.MqttData createValidData() {
        return new MqttPayload.MqttData("dev-01", System.currentTimeMillis(), 45.0f, 24.5f, 55.0f, 90.0f);
    }

    private MqttPayload.MqttData createDataWithSoilMoisture(float value) {
        return new MqttPayload.MqttData("dev-01", System.currentTimeMillis(), value, 24.5f, 55.0f, 90.0f);
    }

    private MqttPayload.MqttData createDataWithTemperature(float value) {
        return new MqttPayload.MqttData("dev-01", System.currentTimeMillis(), 45.0f, value, 55.0f, 90.0f);
    }

    private MqttPayload.MqttData createDataWithHumidity(float value) {
        return new MqttPayload.MqttData("dev-01", System.currentTimeMillis(), 45.0f, 24.5f, value, 90.0f);
    }

    private MqttPayload.MqttData createDataWithBatteryLevel(float value) {
        return new MqttPayload.MqttData("dev-01", System.currentTimeMillis(), 45.0f, 24.5f, 55.0f, value);
    }
}
