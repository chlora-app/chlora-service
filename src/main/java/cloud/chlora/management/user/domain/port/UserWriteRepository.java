package cloud.chlora.management.user.domain.port;

import cloud.chlora.management.user.adapter.in.web.request.UserCreateRequest;
import cloud.chlora.management.user.adapter.in.web.request.UserUpdateRequest;
import cloud.chlora.management.user.domain.model.User;

/**
 * Outbound port — delegates write operations to the auth service over HTTP.
 * Management never owns writes on the users table.
 */
public interface UserWriteRepository {

    User create(UserCreateRequest request);

    User update(String userId, UserUpdateRequest request);

    void softDelete(String userId);

    void restore(String userId);
}