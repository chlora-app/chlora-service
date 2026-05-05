package cloud.chlora.management.device.adapter.out.persistence.entity;

import cloud.chlora.management.device.domain.model.DeviceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@Getter
@Immutable
@NoArgsConstructor
@Table(name = "devices")
@Entity(name = "DeviceReadEntity")
public class DeviceReadEntity {

    @Id
    @Column(name = "device_id", insertable = false, updatable = false)
    private String deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeviceStatus status;

    @Column(name = "pot_id")
    private String potId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}