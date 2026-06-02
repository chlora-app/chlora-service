package cloud.chlora.management.user.adapter.out.persistence.repository;

import cloud.chlora.management.user.adapter.out.persistence.entity.UserReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileReadJpaRepository extends JpaRepository<UserReadEntity, Long> {
    Optional<UserReadEntity> findByUserId(String userId);
}