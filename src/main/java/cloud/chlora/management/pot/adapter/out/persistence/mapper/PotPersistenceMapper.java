package cloud.chlora.management.pot.adapter.out.persistence.mapper;

import cloud.chlora.management.pot.adapter.out.persistence.entity.PotReadEntity;
import cloud.chlora.management.pot.domain.model.Pot;

public final class PotPersistenceMapper {

    private PotPersistenceMapper() {}

    public static Pot toDomain(PotReadEntity e) {
        return new Pot(
                e.getPotId(),
                e.getPotName(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt()
        );
    }
}