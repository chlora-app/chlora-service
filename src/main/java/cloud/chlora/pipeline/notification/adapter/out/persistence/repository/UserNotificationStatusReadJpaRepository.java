package cloud.chlora.pipeline.notification.adapter.out.persistence.repository;

import cloud.chlora.pipeline.notification.adapter.out.persistence.entity.UserNotificationStatusReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserNotificationStatusReadJpaRepository extends JpaRepository<UserNotificationStatusReadEntity, Long> {

    @Query(value = """
            SELECT COUNT(*) FROM user_notification_status
            WHERE user_id = :userId AND is_read = false
            """, nativeQuery = true)
    long countUnreadByUserId(@Param("userId") String userId);
}