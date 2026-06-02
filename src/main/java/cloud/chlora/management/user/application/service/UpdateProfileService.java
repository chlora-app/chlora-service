package cloud.chlora.management.user.application.service;

import cloud.chlora.management.user.adapter.in.web.request.UpdateProfileRequest;
import cloud.chlora.management.user.application.usecase.UpdateProfileUseCase;
import cloud.chlora.management.user.domain.model.User;
import cloud.chlora.management.user.domain.port.UserProfileWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateProfileService implements UpdateProfileUseCase {

    private final UserProfileWriteRepository repository;

    @Override
    public User execute(String userId, UpdateProfileRequest request) {
        return repository.updateProfile(userId, request);
    }
}