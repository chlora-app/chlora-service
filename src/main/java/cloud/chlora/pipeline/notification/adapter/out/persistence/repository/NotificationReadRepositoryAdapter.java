package cloud.chlora.pipeline.notification.adapter.out.persistence.repository;

import cloud.chlora.pipeline.notification.domain.model.NotificationView;
import cloud.chlora.pipeline.notification.domain.port.NotificationReadRepository;
import cloud.chlora.pipeline.shared.NotificationSeverity;
import cloud.chlora.pipeline.shared.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationReadRepositoryAdapter implements NotificationReadRepository {

    private final NotificationReadJpaRepository jpaRepository;

    @Override
    public List<NotificationView> findAllByUserId(String userId) {
        return jpaRepository.findAllByUserId(userId).stream()
                .map(p -> new NotificationView(
                        p.getNotificationId(),
                        p.getMessage(),
                        NotificationSeverity.valueOf(p.getSeverity()),
                        NotificationType.valueOf(p.getNotificationType()),
                        p.getCreatedAt(),
                        p.getIsRead()
                ))
                .toList();
    }
}