package cloud.chlora.management.user.application.service;

import cloud.chlora.management.shared.error.UserErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import cloud.chlora.management.user.adapter.in.web.request.ChangePasswordRequest;
import cloud.chlora.management.user.application.usecase.ChangePasswordUseCase;
import cloud.chlora.management.user.domain.model.User;
import cloud.chlora.management.user.domain.port.UserProfileReadRepository;
import cloud.chlora.management.user.domain.port.UserProfileWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserProfileReadRepository readRepository;
    private final UserProfileWriteRepository writeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void execute(String userId, ChangePasswordRequest request) {
        User user = readRepository.getProfile(userId);

        if (!passwordEncoder.matches(request.oldPassword(), user.password())) {
            throw AppException.of(UserErrorCode.INVALID_PASSWORD);
        }

        String hashedPassword = passwordEncoder.encode(request.newPassword());
        writeRepository.changePassword(userId, hashedPassword);
    }
}