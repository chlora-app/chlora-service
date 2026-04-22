package cloud.chlora.management.pot.adapter.out.persistence.repository;

import cloud.chlora.management.pot.adapter.in.web.request.PotCreateRequest;
import cloud.chlora.management.pot.adapter.in.web.request.PotUpdateRequest;
import cloud.chlora.management.pot.adapter.out.persistence.entity.PotWriteEntity;
import cloud.chlora.management.pot.domain.model.Pot;
import cloud.chlora.management.pot.domain.port.PotWriteRepository;
import cloud.chlora.management.shared.error.IotErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class PotWriteRepositoryAdapter implements PotWriteRepository {

    private final PotWriteJpaRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Pot create(PotCreateRequest request) {
        PotWriteEntity entity = PotWriteEntity.builder()
                .potName(request.potName())
                .build();

        return toDomain(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Pot update(String potId, PotUpdateRequest request) {
        PotWriteEntity entity = repository.findByPotId(potId)
                .orElseThrow(() -> AppException.of(IotErrorCode.POT_NOT_FOUND));

        entity.setPotName(request.potName());

        return toDomain(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void softDelete(String potId) {
        repository.softDelete(potId, Instant.now());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void softDeleteDevicesByPotId(String potId) {
        repository.softDeleteDevices(potId, Instant.now());
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private Pot toDomain(PotWriteEntity e) {
        return new Pot(
                e.getId(),
                e.getPotId(),
                e.getPotName(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt()
        );
    }
}