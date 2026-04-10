package cloud.chlora.management.device.adapter.out.persistence.repository;

import cloud.chlora.management.device.adapter.out.persistence.entity.DeviceReadEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeviceReadJpaRepository extends JpaRepository<DeviceReadEntity, Long> {

    Optional<DeviceReadEntity> findByDeviceId(String deviceId);

    @Query(value = """
            SELECT * FROM devices d
            WHERE d.deleted_at IS NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(d.device_id)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(d.device_name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(d.device_type) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (CAST(:clusterId AS text) IS NULL OR d.cluster_id = :clusterId)
              AND (CAST(:status AS text) IS NULL OR d.status::text = :status)
            """,
            countQuery = """
            SELECT COUNT(*) FROM devices d
            WHERE d.deleted_at IS NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(d.device_id)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(d.device_name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(d.device_type) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (CAST(:clusterId AS text) IS NULL OR d.cluster_id = :clusterId)
              AND (CAST(:status AS text) IS NULL OR d.status::text = :status)
            """,
            nativeQuery = true)
    Page<@NonNull DeviceReadEntity> findAllFiltered(
            @Param("search")    String search,
            @Param("clusterId") String clusterId,
            @Param("status")    String status,
            Pageable pageable
    );

    @Query(value = """
            SELECT * FROM devices WHERE cluster_id = :clusterId AND deleted_at IS NULL
            """, nativeQuery = true)
    java.util.List<DeviceReadEntity> findAllByClusterIdActive(@Param("clusterId") String clusterId);
}