package cloud.chlora.management.user.application.usecase;

import cloud.chlora.management.user.adapter.in.web.request.ChangePasswordRequest;

public interface ChangePasswordUseCase {
    void execute(String userId, ChangePasswordRequest request);
}
