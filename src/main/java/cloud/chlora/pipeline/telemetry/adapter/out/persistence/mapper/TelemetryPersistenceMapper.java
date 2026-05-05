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

    public static Telemetry toDomain(TelemetryEntity entity) {
        return Telemetry.builder()
                .id(entity.getId())
                .deviceId(entity.getDeviceId())
                .deviceTimestamp(entity.getDeviceTimestamp())
                .soilMoisture(entity.getSoilMoisture())
                .temperature(entity.getTemperature())
                .humidity(entity.getHumidity())
                .batteryLevel(entity.getBatteryLevel())
                .receivedAt(entity.getReceivedAt())
                .isValid(entity.isValid())
                .build();
    }
}