package cloud.chlora.pipeline.notification.adapter.out.persistence.repository;

import cloud.chlora.pipeline.notification.domain.port.UserNotificationStatusReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserNotificationStatusReadRepositoryAdapter implements UserNotificationStatusReadRepository {

    private final UserNotificationStatusReadJpaRepository jpaRepository;

    @Override
    public long countUnread(String userId) {
        return jpaRepository.countUnreadByUserId(userId);
    }
}