package cloud.chlora.pipeline.anomaly.adapter.out.persistence;

import cloud.chlora.pipeline.shared.event.AnomalySeverity;
import cloud.chlora.pipeline.shared.event.AnomalyType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "anomalies")
@Entity(name = "AnomalyEntity")
public class AnomalyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "anomaly_type", nullable = false, length = 64)
    private AnomalyType anomalyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 16)
    private AnomalySeverity severity;

    @Column(name = "anomaly_score", nullable = false)
    private float anomalyScore;

    /**
     * Model identifier, e.g. "ISOLATION_FOREST", "LSTM_AUTOENCODER".
     */
    @Column(name = "detected_by", nullable = false, length = 64)
    private String detectedBy;

    /**
     * Deployed model version string, e.g. "1.0.0".
     */
    @Column(name = "model_version", nullable = false, length = 32)
    private String modelVersion;

    @Column(name = "detected_at", nullable = false, updatable = false)
    private Instant detectedAt;

    /**
     * References telemetry.id — no DB-level FK (cross-module boundary).
     */
    @Column(name = "telemetry_id", nullable = false)
    private Long telemetryId;

    /**
     * References notifications.notification_id — nullable because
     * not every anomaly necessarily triggers a notification.
     */
    @Column(name = "notification_id", length = 16)
    private String notificationId;

    @PrePersist
    void prePersist() {
        if (detectedAt == null) {
            detectedAt = Instant.now();
        }
    }
}