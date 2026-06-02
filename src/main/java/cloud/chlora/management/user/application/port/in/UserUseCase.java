package cloud.chlora.management.user.application.port.in;

import cloud.chlora.management.user.adapter.in.web.request.UserCreateRequest;
import cloud.chlora.management.user.adapter.in.web.request.UserUpdateRequest;
import cloud.chlora.management.user.adapter.in.web.response.*;
import cloud.chlora.management.user.domain.model.UserRole;

public interface UserUseCase {

    PagedUserResponse<UserGetResponse> findAllActive(
            int page, int size, String search, String sort, String order, UserRole role
    );

    PagedUserResponse<UserDeletedResponse> findAllDeleted(
            int page, int size, String search, String sort, String order, UserRole role
    );

    UserGetResponse findByUserId(String userId);

    UserCreateResponse createUser(UserCreateRequest request);

    UserUpdateResponse updateUser(String userId, UserUpdateRequest request);

    void deleteUser(String userId);

    void restoreUser(String userId);
}