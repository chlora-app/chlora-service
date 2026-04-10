package cloud.chlora.management.cluster.adapter.out.persistence.repository;

import cloud.chlora.management.cluster.adapter.out.persistence.entity.ClusterReadEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClusterReadJpaRepository extends JpaRepository<ClusterReadEntity, Long> {

    Optional<ClusterReadEntity> findByClusterId(String clusterId);

    @Query(value = """
            SELECT * FROM clusters c
            WHERE c.deleted_at IS NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(c.cluster_id)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.cluster_name) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
            countQuery = """
            SELECT COUNT(*) FROM clusters c
            WHERE c.deleted_at IS NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(c.cluster_id)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.cluster_name) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
            nativeQuery = true)
    Page<@NonNull ClusterReadEntity> findAllExisting(
            @Param("search") String search,
            Pageable pageable
    );

    @Query(value = """
            SELECT * FROM clusters WHERE deleted_at IS NULL
            """, nativeQuery = true)
    java.util.List<ClusterReadEntity> findAllAsList();

    @Query(value = """
            SELECT COUNT(*) FROM devices
            WHERE cluster_id = :clusterId AND deleted_at IS NULL
            """, nativeQuery = true)
    long countDevicesByClusterId(@Param("clusterId") String clusterId);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM clusters
            WHERE cluster_name = :clusterName AND deleted_at IS NULL
            """, nativeQuery = true)
    boolean existsByClusterNameActive(@Param("clusterName") String clusterName);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM clusters
            WHERE cluster_id = :clusterId AND deleted_at IS NULL
            """, nativeQuery = true)
    boolean existsByClusterIdActive(@Param("clusterId") String clusterId);
}