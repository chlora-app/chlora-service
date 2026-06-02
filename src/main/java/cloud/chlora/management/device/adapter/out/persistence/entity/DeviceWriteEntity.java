package cloud.chlora.management.device.adapter.out.persistence.entity;

import cloud.chlora.management.device.domain.model.DeviceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "devices")
@Entity(name = "DeviceWriteEntity")
public class DeviceWriteEntity {

    @Id
    @Column(name = "device_id", updatable = false)
    private String deviceId;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeviceStatus status;

    @Column(name = "pot_id", nullable = false)
    private String potId;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = DeviceStatus.OFFLINE;
    }

    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }
}