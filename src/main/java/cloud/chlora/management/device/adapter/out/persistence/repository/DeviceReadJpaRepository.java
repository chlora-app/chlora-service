package cloud.chlora.management.device.adapter.out.persistence.repository;

import cloud.chlora.management.device.adapter.out.persistence.entity.DeviceReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DeviceReadJpaRepository extends JpaRepository<DeviceReadEntity, Long> {

    interface DeviceWithPotProjection {
        String getDeviceId();
        String getDeviceName();
        String getStatus();
        String getPotId();
        String getPotName();
        Instant getCreatedAt();
        Instant getUpdatedAt();
        Instant getDeletedAt();
    }

    Optional<DeviceReadEntity> findByDeviceId(String deviceId);

    @Query(value = """
            SELECT
                d.device_id   AS deviceId,
                d.device_name AS deviceName,
                d.status      AS status,
                d.pot_id      AS potId,
                p.pot_name    AS potName,
                d.created_at  AS createdAt,
                d.updated_at  AS updatedAt,
                d.deleted_at  AS deletedAt
            FROM devices d
            LEFT JOIN pots p ON p.pot_id = d.pot_id AND p.deleted_at IS NULL
            WHERE d.deleted_at IS NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(d.device_id)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(d.device_name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (CAST(:potId AS text) IS NULL OR d.pot_id = :potId)
              AND (CAST(:status AS text) IS NULL OR d.status::text = :status)
            ORDER BY
                CASE WHEN :sortCol = 'device_id'   AND :sortDir = 'ASC'  THEN d.device_id   END ASC,
                CASE WHEN :sortCol = 'device_id'   AND :sortDir = 'DESC' THEN d.device_id   END DESC,
                CASE WHEN :sortCol = 'device_name' AND :sortDir = 'ASC'  THEN d.device_name END ASC,
                CASE WHEN :sortCol = 'device_name' AND :sortDir = 'DESC' THEN d.device_name END DESC,
                CASE WHEN :sortCol = 'status'      AND :sortDir = 'ASC'  THEN d.status::text END ASC,
                CASE WHEN :sortCol = 'status'      AND :sortDir = 'DESC' THEN d.status::text END DESC,
                CASE WHEN :sortCol = 'pot_id'      AND :sortDir = 'ASC'  THEN d.pot_id      END ASC,
                CASE WHEN :sortCol = 'pot_id'      AND :sortDir = 'DESC' THEN d.pot_id      END DESC,
                CASE WHEN :sortCol = 'pot_name'    AND :sortDir = 'ASC'  THEN p.pot_name    END ASC,
                CASE WHEN :sortCol = 'pot_name'    AND :sortDir = 'DESC' THEN p.pot_name    END DESC,
                CASE WHEN :sortCol = 'created_at'  AND :sortDir = 'ASC'  THEN d.created_at  END ASC,
                CASE WHEN :sortCol = 'created_at'  AND :sortDir = 'DESC' THEN d.created_at  END DESC
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<DeviceWithPotProjection> findAllFiltered(
            @Param("search")  String search,
            @Param("potId")   String potId,
            @Param("status")  String status,
            @Param("sortCol") String sortCol,
            @Param("sortDir") String sortDir,
            @Param("limit")   int limit,
            @Param("offset")  int offset
    );

    @Query(value = """
            SELECT COUNT(*) FROM devices d
            WHERE d.deleted_at IS NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(d.device_id)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(d.device_name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (CAST(:potId AS text) IS NULL OR d.pot_id = :potId)
              AND (CAST(:status AS text) IS NULL OR d.status::text = :status)
            """,
            nativeQuery = true)
    long countAllFiltered(
            @Param("search") String search,
            @Param("potId")  String potId,
            @Param("status") String status
    );

    @Query(value = """
            SELECT * FROM devices WHERE pot_id = :potId AND deleted_at IS NULL
            """, nativeQuery = true)
    List<DeviceReadEntity> findAllByPotIdActive(@Param("potId") String potId);
}