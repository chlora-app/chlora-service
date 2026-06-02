package cloud.chlora.management.user.domain.port;

import cloud.chlora.management.user.domain.model.User;

public interface UserProfileReadRepository {
    User getProfile(String userId);
}