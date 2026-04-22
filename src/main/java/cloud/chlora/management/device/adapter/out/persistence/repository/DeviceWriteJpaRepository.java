package cloud.chlora.management.device.adapter.out.persistence.repository;

import cloud.chlora.management.device.adapter.out.persistence.entity.DeviceWriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface DeviceWriteJpaRepository extends JpaRepository<DeviceWriteEntity, Long> {

    Optional<DeviceWriteEntity> findByDeviceId(String deviceId);

    @Modifying
    @Query("UPDATE DeviceWriteEntity d SET d.deletedAt = :now WHERE d.deviceId = :deviceId AND d.deletedAt IS NULL")
    int softDelete(@Param("deviceId") String deviceId, @Param("now") Instant now);

    @Modifying
    @Query(value = """
            UPDATE devices
            SET status = 'OFFLINE', updated_at = NOW()
            WHERE deleted_at IS NULL
              AND status = 'ONLINE'
              AND (last_seen_at IS NULL OR last_seen_at < :threshold)
            """, nativeQuery = true)
    int setOfflineIfStale(@Param("threshold") Instant threshold);
}