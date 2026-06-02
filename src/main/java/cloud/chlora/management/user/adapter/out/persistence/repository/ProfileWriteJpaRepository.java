package cloud.chlora.management.user.adapter.out.persistence.repository;

import cloud.chlora.management.user.adapter.out.persistence.entity.UserWriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProfileWriteJpaRepository extends JpaRepository<UserWriteEntity, Long> {

    Optional<UserWriteEntity> findByUserId(String userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserWriteEntity u SET u.password = :password WHERE u.userId = :userId AND u.deletedAt IS NULL")
    void changePassword(@Param("userId") String userId, @Param("password") String hashedPassword);
}