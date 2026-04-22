package cloud.chlora.pipeline.telemetry.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "telemetry")
@Entity(name = "TelemetryEntity")
public class TelemetryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "device_timestamp", nullable = false)
    private Instant deviceTimestamp;

    @Column(name = "soil_moisture", nullable = false)
    private float soilMoisture;

    @Column(name = "temperature", nullable = false)
    private float temperature;

    @Column(name = "humidity", nullable = false)
    private float humidity;

    @Column(name = "battery_level", nullable = false)
    private float batteryLevel;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "is_valid", nullable = false)
    private boolean isValid;
}