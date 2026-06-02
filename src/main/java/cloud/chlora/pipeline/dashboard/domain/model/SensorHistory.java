package cloud.chlora.pipeline.dashboard.domain.model;

import lombok.Builder;

import java.util.List;

@Builder
public record SensorHistory(
        String potId,
        String potName,
        String deviceName,
        String range,
        String interval,
        List<SensorAggregatePoint> data
) {

    @Builder
    public record SensorAggregatePoint(
            String time,
            float temperature,
            float humidity,
            float soilMoisture
    ) {}
}
