package cloud.chlora.pipeline.shared.event;

import cloud.chlora.pipeline.shared.ValidationResult;

public record TelemetryProcessedEvent(
        ProcessedTelemetryEvent telemetry,
        ValidationResult validationResult
) {}