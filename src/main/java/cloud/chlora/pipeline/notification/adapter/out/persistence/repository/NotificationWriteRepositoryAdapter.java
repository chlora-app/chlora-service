package cloud.chlora.pipeline.notification.adapter.out.persistence.repository;

import cloud.chlora.pipeline.notification.adapter.out.persistence.mapper.NotificationPersistenceMapper;
import cloud.chlora.pipeline.notification.domain.model.Notification;
import cloud.chlora.pipeline.notification.domain.port.NotificationWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class NotificationWriteRepositoryAdapter implements NotificationWriteRepository {

    private final NotificationWriteJpaRepository jpaRepository;

    @Override
    public Notification save(Notification notification) {
        var entity = NotificationPersistenceMapper.toEntity(notification);
        var saved = jpaRepository.save(entity);
        return NotificationPersistenceMapper.toDomain(saved);
    }

    @Override
    public int deleteOlderThan(Instant cutoff) {
        return jpaRepository.deleteByCreatedAtBefore(cutoff);
    }
}