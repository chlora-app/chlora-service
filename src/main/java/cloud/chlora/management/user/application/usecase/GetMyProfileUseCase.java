package cloud.chlora.management.user.application.usecase;

import cloud.chlora.management.user.domain.model.User;

public interface GetMyProfileUseCase {
    User execute(String userId);
}