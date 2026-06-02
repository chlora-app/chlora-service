package cloud.chlora.pipeline.dashboard.application.usecase;

import cloud.chlora.pipeline.dashboard.domain.model.SensorHistory;

public interface GetSensorHistoryUseCase {
    SensorHistory execute(String potId, String range);
}