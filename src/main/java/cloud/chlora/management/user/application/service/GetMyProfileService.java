package cloud.chlora.management.user.application.service;

import cloud.chlora.management.user.application.usecase.GetMyProfileUseCase;
import cloud.chlora.management.user.domain.model.User;
import cloud.chlora.management.user.domain.port.UserProfileReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMyProfileService implements GetMyProfileUseCase {

    private final UserProfileReadRepository repository;

    @Override
    public User execute(String userId) {
        return repository.getProfile(userId);
    }
}