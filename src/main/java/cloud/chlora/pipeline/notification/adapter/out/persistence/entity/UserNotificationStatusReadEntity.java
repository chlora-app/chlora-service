package cloud.chlora.pipeline.notification.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@Getter
@Immutable
@NoArgsConstructor
@Table(name = "user_notification_status")
@Entity(name = "UserNotificationStatusReadEntity")
public class UserNotificationStatusReadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id")
    private String notificationId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "read_at")
    private Instant readAt;
}