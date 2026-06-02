package cloud.chlora.management.device.adapter.out.persistence.repository;

import cloud.chlora.management.device.adapter.out.persistence.entity.DeviceWriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DeviceRegistrationJpaRepository extends JpaRepository<DeviceWriteEntity, Long> {

    boolean existsByDeviceId(String deviceId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE devices SET status = 'ONLINE', last_seen_at = NOW() WHERE device_id = :deviceId", nativeQuery = true)
    void setDeviceOnline(@Param("deviceId") String deviceId);
}