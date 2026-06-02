package cloud.chlora.management.user.domain.port;

import cloud.chlora.management.user.adapter.in.web.request.UpdateProfileRequest;
import cloud.chlora.management.user.domain.model.User;

public interface UserProfileWriteRepository {
    User updateProfile(String userId, UpdateProfileRequest request);
    void changePassword(String userId, String hashedPassword);
}