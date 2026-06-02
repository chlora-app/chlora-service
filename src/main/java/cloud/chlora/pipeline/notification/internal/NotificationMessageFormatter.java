package cloud.chlora.pipeline.notification.internal;

import cloud.chlora.pipeline.notification.domain.model.BatteryNotificationThreshold;
import cloud.chlora.pipeline.notification.domain.model.Notification;
import cloud.chlora.pipeline.shared.NotificationSeverity;
import cloud.chlora.pipeline.shared.NotificationType;
import cloud.chlora.pipeline.shared.event.ProcessedTelemetryEvent;
import cloud.chlora.pipeline.shared.event.SensorAnomalyDetectedEvent;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageFormatter {

    public Notification formatLowBattery(ProcessedTelemetryEvent event, BatteryNotificationThreshold threshold) {
        return new Notification(
                null,
                event.deviceId(),
                "%s battery is low (%d%% remaining)".formatted(
                        event.deviceId(),
                        Math.round(event.batteryLevel())
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
                    "%s battery is low (%d%% remaining)".formatted(event.deviceId(), Math.round(event.actualValue()));
            case TIMESTAMP_DRIFT ->
                    "%s timestamp drift detected".formatted(event.deviceId());
            case SENSOR_UNRESPONSIVE ->
                    "%s sensor is unresponsive".formatted(event.deviceId());
            case SENSOR_ANOMALY ->
                    "%s sensor anomaly detected".formatted(event.deviceId());
        };

        NotificationSeverity notifSeverity = switch (event.severity()) {
            case LOW      -> NotificationSeverity.INFO;
            case MEDIUM, HIGH -> NotificationSeverity.WARNING;
            case CRITICAL -> NotificationSeverity.CRITICAL;
        };

        return new Notification(
                null,
                event.deviceId(),
                message,
                notifSeverity,
                NotificationType.ANOMALY,
                null
        );
    }
}