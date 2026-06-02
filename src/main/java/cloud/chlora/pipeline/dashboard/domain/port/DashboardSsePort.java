package cloud.chlora.pipeline.dashboard.domain.port;

public interface DashboardSsePort {
    void broadcast(String eventName, Object data);
}