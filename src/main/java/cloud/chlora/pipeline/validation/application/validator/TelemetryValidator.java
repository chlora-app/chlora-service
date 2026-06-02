package cloud.chlora.pipeline.validation.application.validator;

import cloud.chlora.pipeline.ingestion.internal.MqttPayload;
import cloud.chlora.pipeline.shared.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelemetryValidator {

    private static final float SOIL_MOISTURE_MIN = 0f;
    private static final float SOIL_MOISTURE_MAX = 100f;

    private static final float TEMPERATURE_MIN = -40f;
    private static final float TEMPERATURE_MAX = 85f;

    private static final float HUMIDITY_MIN = 0f;
    private static final float HUMIDITY_MAX = 100f;

    private static final float BATTERY_LEVEL_MIN = 0f;
    private static final float BATTERY_LEVEL_MAX = 100f;

    private static final long TIMESTAMP_DRIFT_SECONDS = 300;

    public ValidationResult validate(MqttPayload payload) {
        List<String> violations = new ArrayList<>();

        validateMetadata(payload.metadata(), violations);
        validateData(payload.data(), violations);

        if (!violations.isEmpty()) {
            log.warn("[Validation] Failed for device={}, violations={}",
                    payload.data().deviceId(), violations);
            return ValidationResult.rejected(String.join(", ", violations));
        }

        return ValidationResult.ok();
    }

    // ── Metadata ──────────────────────────────────────────────
    private void validateMetadata(MqttPayload.MqttMetadata metadata, List<String> violations) {
        if (isBlank(metadata.messageId())) {
            violations.add("messageId is blank");
        }
        if (isBlank(metadata.topic())) {
            violations.add("topic is blank");
        }
        if (metadata.receivedAt() == null) {
            violations.add("receivedAt is null");
        }
    }

    // ── Data ──────────────────────────────────────────────────
    private void validateData(MqttPayload.MqttData data, List<String> violations) {
        validateDeviceId(data, violations);
        validateTimestamp(data, violations);
        validateSoilMoisture(data, violations);
        validateTemperature(data, violations);
        validateHumidity(data, violations);
        validateBatteryLevel(data, violations);
    }

    private void validateDeviceId(MqttPayload.MqttData data, List<String> violations) {
        if (isBlank(data.deviceId())) {
            violations.add("deviceId is blank");
        }
    }

    private void validateTimestamp(MqttPayload.MqttData data, List<String> violations) {
        if (data.timestamp() <= 0) {
            violations.add("timestamp must be positive");
            return;
        }

        Instant deviceTime = Instant.ofEpochMilli(data.timestamp());
        Instant now = Instant.now();
        long driftSeconds = Math.abs(Duration.between(deviceTime, now).getSeconds());

        if (driftSeconds > TIMESTAMP_DRIFT_SECONDS) {
            violations.add("timestamp drift too large: %ds (max %ds)"
                    .formatted(driftSeconds, TIMESTAMP_DRIFT_SECONDS));
        }
    }

    private void validateSoilMoisture(MqttPayload.MqttData data, List<String> violations) {
        if (isOutOfRange(data.soilMoisture(), SOIL_MOISTURE_MIN, SOIL_MOISTURE_MAX)) {
            violations.add("soilMoisture out of range [%.1f, %.1f]: %.2f"
                    .formatted(SOIL_MOISTURE_MIN, SOIL_MOISTURE_MAX, data.soilMoisture()));
        }
    }

    private void validateTemperature(MqttPayload.MqttData data, List<String> violations) {
        if (isOutOfRange(data.temperature(), TEMPERATURE_MIN, TEMPERATURE_MAX)) {
            violations.add("temperature out of range [%.1f, %.1f]: %.2f"
                    .formatted(TEMPERATURE_MIN, TEMPERATURE_MAX, data.temperature()));
        }
    }

    private void validateHumidity(MqttPayload.MqttData data, List<String> violations) {
        if (isOutOfRange(data.humidity(), HUMIDITY_MIN, HUMIDITY_MAX)) {
            violations.add("humidity out of range [%.1f, %.1f]: %.2f"
                    .formatted(HUMIDITY_MIN, HUMIDITY_MAX, data.humidity()));
        }
    }

    private void validateBatteryLevel(MqttPayload.MqttData data, List<String> violations) {
        if (isOutOfRange(data.batteryLevel(), BATTERY_LEVEL_MIN, BATTERY_LEVEL_MAX)) {
            violations.add("batteryLevel out of range [%.1f, %.1f]: %.2f"
                    .formatted(BATTERY_LEVEL_MIN, BATTERY_LEVEL_MAX, data.batteryLevel()));
        }
    }

    // ── Helpers ───────────────────────────────────────────────
    private boolean isOutOfRange(float value, float min, float max) {
        return value < min || value > max;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}