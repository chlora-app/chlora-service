package cloud.chlora.pipeline.telemetry.domain.port;

import cloud.chlora.pipeline.telemetry.domain.model.Telemetry;

public interface TelemetryWriteRepository {
    Telemetry save(Telemetry telemetry);
}