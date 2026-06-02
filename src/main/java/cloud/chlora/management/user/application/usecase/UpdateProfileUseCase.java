package cloud.chlora.management.user.application.usecase;

import cloud.chlora.management.user.adapter.in.web.request.UpdateProfileRequest;
import cloud.chlora.management.user.domain.model.User;

public interface UpdateProfileUseCase {
    User execute(String userId, UpdateProfileRequest request);
}
