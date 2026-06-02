package cloud.chlora.pipeline.shared.event;

public record TelemetrySavedEvent(
        Long telemetryId,
        String deviceId,
        float soilMoisture,
        float temperature,
        float humidity
) {}
