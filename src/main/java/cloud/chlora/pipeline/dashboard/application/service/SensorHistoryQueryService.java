package cloud.chlora.pipeline.dashboard.application.service;

import cloud.chlora.pipeline.dashboard.application.usecase.GetSensorHistoryUseCase;
import cloud.chlora.pipeline.dashboard.domain.model.SensorHistory;
import cloud.chlora.pipeline.dashboard.domain.port.SensorHistoryQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorHistoryQueryService implements GetSensorHistoryUseCase {

    private final SensorHistoryQueryRepository repository;

    @Override
    public SensorHistory execute(String potId, String range) {
        return repository.getSensorHistory(potId, range);
    }
}