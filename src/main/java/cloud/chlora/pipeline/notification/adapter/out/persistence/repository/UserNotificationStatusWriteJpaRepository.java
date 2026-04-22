package cloud.chlora.pipeline.notification.adapter.out.persistence.repository;

import cloud.chlora.pipeline.notification.adapter.out.persistence.entity.UserNotificationStatusWriteEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface UserNotificationStatusWriteJpaRepository extends JpaRepository<UserNotificationStatusWriteEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE user_notification_status
            SET is_read = true, read_at = :readAt
            WHERE notification_id = :notificationId AND user_id = :userId
            """, nativeQuery = true)
    void markAsRead(
            @Param("notificationId") String notificationId,
            @Param("userId") String userId,
            @Param("readAt") Instant readAt
    );

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE user_notification_status
            SET is_read = true, read_at = :readAt
            WHERE user_id = :userId AND is_read = false
            """, nativeQuery = true)
    void markAllAsRead(
            @Param("userId") String userId,
            @Param("readAt") Instant readAt
    );
}