package cloud.chlora.pipeline.telemetry.adapter.out.persistence.mapper;

import cloud.chlora.pipeline.telemetry.adapter.out.persistence.entity.TelemetryEntity;
import cloud.chlora.pipeline.telemetry.domain.model.Telemetry;

public final class TelemetryPersistenceMapper {

    private TelemetryPersistenceMapper() {}

    public static TelemetryEntity toEntity(Telemetry t) {
        return TelemetryEntity.builder()
                .deviceId(t.deviceId())
                .deviceTimestamp(t.deviceTimestamp())
                .soilMoisture(t.soilMoisture())
                .temperature(t.temperature())
                .humidity(t.humidity())
                .batteryLevel(t.batteryLevel())
                .receivedAt(t.receivedAt())
                .isValid(t.isValid())
                .build();
    }
}