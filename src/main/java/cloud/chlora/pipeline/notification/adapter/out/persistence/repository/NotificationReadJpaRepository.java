package cloud.chlora.pipeline.notification.adapter.out.persistence.repository;

import cloud.chlora.pipeline.notification.adapter.out.persistence.entity.UserNotificationStatusReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationReadJpaRepository extends JpaRepository<UserNotificationStatusReadEntity, Long> {

    @Query(value = """
            SELECT
                n.notification_id    AS notificationId,
                n.message            AS message,
                n.severity           AS severity,
                n.notification_type  AS notificationType,
                n.created_at         AS createdAt,
                uns.is_read          AS isRead
            FROM user_notification_status uns
            JOIN notifications n ON n.notification_id = uns.notification_id
            WHERE uns.user_id = :userId
            ORDER BY n.created_at DESC
            """, nativeQuery = true)
    List<NotificationListProjection> findAllByUserId(@Param("userId") String userId);
}