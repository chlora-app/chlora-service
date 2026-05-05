package cloud.chlora.pipeline.telemetry.application.usecase;

import cloud.chlora.pipeline.shared.ValidationResult;
import cloud.chlora.pipeline.shared.event.ProcessedTelemetryEvent;

public interface SaveTelemetryUseCase {
    void execute(ProcessedTelemetryEvent event, ValidationResult validationResult);
}