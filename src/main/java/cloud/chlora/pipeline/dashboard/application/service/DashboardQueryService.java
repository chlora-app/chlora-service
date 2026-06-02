package cloud.chlora.pipeline.dashboard.application.service;

import cloud.chlora.pipeline.dashboard.application.usecase.GetDashboardSnapshotUseCase;
import cloud.chlora.pipeline.dashboard.domain.model.DashboardSnapshot;
import cloud.chlora.pipeline.dashboard.domain.port.DashboardQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardQueryService implements GetDashboardSnapshotUseCase {

    private final DashboardQueryRepository repository;

    @Override
    public DashboardSnapshot execute() {
        return repository.getSnapshot();
    }
}
