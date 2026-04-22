package cloud.chlora.pipeline.telemetry.domain.port;

import cloud.chlora.pipeline.telemetry.domain.model.Telemetry;

public interface TelemetryWriteRepository {

    void save(Telemetry telemetry);
}