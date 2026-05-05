package cloud.chlora.pipeline.dashboard.application.service;

import cloud.chlora.pipeline.dashboard.adapter.out.sse.DashboardSseRegistry;
import cloud.chlora.pipeline.dashboard.application.usecase.GetDashboardSnapshotUseCase;
import cloud.chlora.pipeline.dashboard.domain.model.DashboardSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardSseService {

    private final GetDashboardSnapshotUseCase snapshotUseCase;
    private final DashboardSseRegistry registry;

    public DashboardSnapshot getSnapshot() {
        return snapshotUseCase.execute();
    }

    public void broadcastSnapshot() {
        DashboardSnapshot snapshot = snapshotUseCase.execute();
        registry.broadcast("dashboard-update", snapshot);
    }
}