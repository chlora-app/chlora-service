package cloud.chlora.pipeline.notification.adapter.out.persistence.repository;

import cloud.chlora.pipeline.notification.adapter.out.persistence.entity.UserNotificationStatusWriteEntity;
import cloud.chlora.pipeline.notification.domain.model.Notification;
import cloud.chlora.pipeline.notification.domain.port.UserNotificationStatusWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserNotificationStatusWriteRepositoryAdapter implements UserNotificationStatusWriteRepository {

    private final UserNotificationStatusWriteJpaRepository jpaRepository;

    @Override
    public void fanOut(Notification notification, List<String> userIds) {
        var entities = userIds.stream()
                .map(userId -> UserNotificationStatusWriteEntity.builder()
                        .notificationId(notification.notificationId())
                        .userId(userId)
                        .isRead(false)
                        .build())
                .toList();

        jpaRepository.saveAll(entities);
    }

    @Override
    public void markAsRead(String notificationId, String userId) {
        jpaRepository.markAsRead(notificationId, userId, Instant.now());
    }

    @Override
    public void markAllAsRead(String userId) {
        jpaRepository.markAllAsRead(userId, Instant.now());
    }
}