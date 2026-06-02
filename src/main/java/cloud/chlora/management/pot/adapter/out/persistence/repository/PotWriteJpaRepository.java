package cloud.chlora.management.pot.adapter.out.persistence.repository;

import cloud.chlora.management.pot.adapter.out.persistence.entity.PotWriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface PotWriteJpaRepository extends JpaRepository<PotWriteEntity, Long> {

    Optional<PotWriteEntity> findByPotId(String potId);

    @Modifying
    @Query("UPDATE PotWriteEntity p SET p.deletedAt = :now WHERE p.potId = :potId AND p.deletedAt IS NULL")
    int softDelete(@Param("potId") String potId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE DeviceWriteEntity d SET d.deletedAt = :now WHERE d.potId = :potId AND d.deletedAt IS NULL")
    void softDeleteDevices(@Param("potId") String potId, @Param("now") Instant now);
}