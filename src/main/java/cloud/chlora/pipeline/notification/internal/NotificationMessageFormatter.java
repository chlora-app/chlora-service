package cloud.chlora.pipeline.notification.internal;

import cloud.chlora.pipeline.notification.domain.model.BatteryNotificationThreshold;
import cloud.chlora.pipeline.notification.domain.model.Notification;
import cloud.chlora.pipeline.shared.NotificationType;
import cloud.chlora.pipeline.shared.event.ProcessedTelemetryEvent;
import cloud.chlora.pipeline.shared.event.SensorAnomalyDetectedEvent;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageFormatter {

    public Notification formatLowBattery(ProcessedTelemetryEvent event, BatteryNotificationThreshold threshold) {
        return new Notification(
                null,
                null,
                event.deviceId(),
                "%s battery is low (%.0f%% remaining)".formatted(
                        event.deviceId(),
                        event.batteryLevel()
                ),
                threshold.getSeverity(),
                NotificationType.BATTERY,
                null
        );
    }

    public Notification formatAnomaly(SensorAnomalyDetectedEvent event) {
        String message = switch (event.type()) {
            case TEMPERATURE_OUT_OF_RANGE ->
                    "%s temperature anomaly detected (%.1f°C)".formatted(event.deviceId(), event.actualValue());
            case HUMIDITY_OUT_OF_RANGE ->
                    "%s humidity anomaly detected (%.1f%%)".formatted(event.deviceId(), event.actualValue());
            case SOIL_MOISTURE_OUT_OF_RANGE ->
                    "%s soil moisture anomaly detected (%.1f%%)".formatted(event.deviceId(), event.actualValue());
            case BATTERY_LOW ->
                    "%s battery is low (%.0f%% remaining)".formatted(event.deviceId(), event.actualValue());
            case TIMESTAMP_DRIFT ->
                    "%s timestamp drift detected".formatted(event.deviceId());
            case SENSOR_UNRESPONSIVE ->
                    "%s sensor is unresponsive".formatted(event.deviceId());
        };

        return new Notification(
                null,
                null,
                event.deviceId(),
                message,
                cloud.chlora.pipeline.shared.NotificationSeverity.CRITICAL,
                NotificationType.ANOMALY,
                null
        );
    }
}