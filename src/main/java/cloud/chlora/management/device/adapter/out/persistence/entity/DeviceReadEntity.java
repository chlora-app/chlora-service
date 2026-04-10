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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", insertable = false, updatable = false)
    private String deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "status")
    private DeviceStatus status;

    @Column(name = "cluster_id")
    private String clusterId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}