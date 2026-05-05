package cloud.chlora.pipeline.dashboard.adapter.out.persistence;

import java.time.Instant;

public interface DashboardQueryProjection {
    String getPotId();
    String getPotName();
    Boolean getIsOnline();
    Float getTemperature();
    Float getBatteryLevel();
    Float getSoilMoisture();
    Float getHumidity();
    Integer getAnomalyCount();
    Instant getLastUpdated();
}
