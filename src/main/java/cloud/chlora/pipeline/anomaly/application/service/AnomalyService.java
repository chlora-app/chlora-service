package cloud.chlora.pipeline.anomaly.application.service;

import cloud.chlora.pipeline.anomaly.application.usecase.UpdateAnomalyUseCase;
import cloud.chlora.pipeline.anomaly.domain.model.Anomaly;
import cloud.chlora.pipeline.anomaly.domain.model.DetectionRequest;
import cloud.chlora.pipeline.anomaly.domain.model.DetectionResponse;
import cloud.chlora.pipeline.anomaly.domain.port.AnomalyDetectionPort;
import cloud.chlora.pipeline.anomaly.domain.port.AnomalyPersistencePort;
import cloud.chlora.pipeline.shared.event.SensorAnomalyDetectedEvent;
import cloud.chlora.shared.enums.AnomalySeverity;
import cloud.chlora.shared.enums.AnomalyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnomalyService implements UpdateAnomalyUseCase {

    private final AnomalyDetectionPort detectionPort;
    private final AnomalyPersistencePort persistencePort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void update(DetectionRequest request, Long telemetryId) {
        DetectionResponse response = detectionPort.detect(request);
        if (!response.isAnomaly()) {
            log.info("[Anomaly] No anomaly detected telemetryId={} deviceId={}", telemetryId, request.deviceId());
            return;
        }

        log.warn("[Anomaly] Anomaly detected telemetryId={} deviceId={} severity={} score={}",
                telemetryId, request.deviceId(), response.severity(), response.anomalyScore());

        Anomaly anomaly = Anomaly.builder()
                .id(null)
                .anomalyType(AnomalyType.SENSOR_ANOMALY)
                .severity(AnomalySeverity.valueOf(response.severity()))
                .anomalyScore(response.anomalyScore())
                .detectedBy(response.detectedBy())
                .modelVersion(response.modelVersion())
                .detectedAt(Instant.now())
                .telemetryId(telemetryId)
                .notificationId(null)
                .build();

        persistencePort.save(anomaly);

        eventPublisher.publishEvent(new SensorAnomalyDetectedEvent(
                request.deviceId(),
                Instant.now(),
                anomaly.detectedAt(),
                anomaly.anomalyType(),
                anomaly.severity(),
                null,
                0f,
                0f,
                0f
        ));
    }
}