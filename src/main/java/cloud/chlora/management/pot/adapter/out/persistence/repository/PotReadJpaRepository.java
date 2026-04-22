package cloud.chlora.management.pot.adapter.out.persistence.repository;

import cloud.chlora.management.pot.adapter.out.persistence.entity.PotReadEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PotReadJpaRepository extends JpaRepository<PotReadEntity, Long> {

    interface PotIdNameProjection {
        String getPotId();
        String getPotName();
    }

    Optional<PotReadEntity> findByPotId(String potId);

    @Query(value = "SELECT pot_name FROM pots WHERE pot_id = :potId", nativeQuery = true)
    String getPotName(@Param("potId") String potId);

    @Query(value = "SELECT pot_id AS potId, pot_name AS potName FROM pots WHERE pot_id IN :potIds", nativeQuery = true)
    List<PotIdNameProjection> getPotNames(@Param("potIds") Set<String> potIds);

    @Query(value = """
            SELECT
                p.pot_id      AS potId,
                p.pot_name    AS potName,
                EXISTS (
                    SELECT 1 FROM devices d
                    WHERE d.pot_id = p.pot_id
                    AND d.deleted_at IS NULL
                ) AS isMonitored
            FROM pots p
            WHERE p.deleted_at IS NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(p.pot_id)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(p.pot_name) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
            countQuery = """
            SELECT COUNT(*) FROM pots p
            WHERE p.deleted_at IS NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(p.pot_id)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(p.pot_name) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
            nativeQuery = true)
    Page<PotSummaryProjection> findAllExisting(
            @Param("search") String search,
            Pageable pageable
    );

    @Query(value = """
            SELECT * FROM pots WHERE deleted_at IS NULL
            """, nativeQuery = true)
    List<PotReadEntity> findAllAsList();

    @Query(value = """
            SELECT COUNT(*) FROM devices
            WHERE pot_id = :potId AND deleted_at IS NULL
            """, nativeQuery = true)
    long countDevicesByPotId(@Param("potId") String potId);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM pots
            WHERE pot_name = :potName AND deleted_at IS NULL
            """, nativeQuery = true)
    boolean existsByPotNameActive(@Param("potName") String potName);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM pots
            WHERE pot_id = :potId AND deleted_at IS NULL
            """, nativeQuery = true)
    boolean existsByPotIdActive(@Param("potId") String potId);
}