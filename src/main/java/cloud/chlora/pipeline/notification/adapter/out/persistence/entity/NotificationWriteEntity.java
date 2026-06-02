package cloud.chlora.pipeline.notification.adapter.out.persistence.entity;

import cloud.chlora.pipeline.shared.NotificationSeverity;
import cloud.chlora.pipeline.shared.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
@Entity(name = "NotificationWriteEntity")
public class NotificationWriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id", updatable = false)
    private String notificationId;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "message", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private NotificationSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (notificationId == null) {
            notificationId = "N-" + RandomStringUtils.secure().nextAlphanumeric(8).toLowerCase();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}