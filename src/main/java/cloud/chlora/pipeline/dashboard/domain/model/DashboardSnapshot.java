package cloud.chlora.pipeline.dashboard.domain.model;

import java.time.Instant;
import java.util.List;

public record DashboardSnapshot(
        PotStatus potStatus,
        AnomalySummary anomalySummary,
        List<PotCard> pots
) {
    public record PotStatus(
            int onlineCount,
            int offlineCount
    ) {}

    public record AnomalySummary(Today today, ThisWeek thisWeek, LastDetected lastDetected) {
        public record Today(int current, int previous) {}
        public record ThisWeek(int current, int previous) {}
        public record LastDetected(String potId, String potName, Instant timestamp) {}
    }

    public record PotCard(
            String potId,
            String potName,
            boolean isOnline,
            float temperature,
            float battery,
            float soilMoisture,
            float humidity,
            int anomalyCount,
            Instant lastUpdated
    ) {}
}
