package cloud.chlora.management.user.adapter.out.persistence.repository;

import cloud.chlora.management.user.adapter.out.persistence.entity.UserWriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface UserWriteJpaRepository extends JpaRepository<UserWriteEntity, Long> {

    Optional<UserWriteEntity> findByUserId(String userId);

    @Modifying
    @Query("UPDATE UserWriteEntity u SET u.deletedAt = :now WHERE u.userId = :userId AND u.deletedAt IS NULL")
    int softDelete(@Param("userId") String userId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE UserWriteEntity u SET u.deletedAt = NULL, u.updatedAt = :now WHERE u.userId = :userId")
    int restore(@Param("userId") String userId, @Param("now") Instant now);
}