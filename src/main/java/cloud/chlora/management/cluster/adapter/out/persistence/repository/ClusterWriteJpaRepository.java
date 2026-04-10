package cloud.chlora.management.cluster.adapter.out.persistence.repository;

import cloud.chlora.management.cluster.adapter.out.persistence.entity.ClusterWriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface ClusterWriteJpaRepository extends JpaRepository<ClusterWriteEntity, Long> {

    Optional<ClusterWriteEntity> findByClusterId(String clusterId);

    @Modifying
    @Query("UPDATE ClusterWriteEntity c SET c.deletedAt = :now WHERE c.clusterId = :clusterId AND c.deletedAt IS NULL")
    int softDelete(@Param("clusterId") String clusterId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE DeviceWriteEntity d SET d.deletedAt = :now WHERE d.clusterId = :clusterId AND d.deletedAt IS NULL")
    void softDeleteDevices(@Param("clusterId") String clusterId, @Param("now") Instant now);
}