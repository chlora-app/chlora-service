package cloud.chlora.pipeline.dashboard.domain.port;

import cloud.chlora.pipeline.dashboard.domain.model.DashboardSnapshot;

public interface DashboardQueryRepository {
    DashboardSnapshot getSnapshot();
}
