package cloud.chlora.pipeline.dashboard.domain.port;

import cloud.chlora.pipeline.dashboard.domain.model.SensorHistory;

public interface SensorHistoryQueryRepository {
    SensorHistory getSensorHistory(String potId, String range);
}
