package cloud.chlora.management.user.adapter.out.persistence.mapper;

import cloud.chlora.management.user.adapter.out.persistence.entity.UserReadEntity;
import cloud.chlora.management.user.domain.model.User;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {}

    public static User toDomain(UserReadEntity e) {
        return new User(
                e.getId(),
                e.getUserId(),
                e.getEmail(),
                e.getPassword(),
                e.getName(),
                e.getRole(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt()
        );
    }
}