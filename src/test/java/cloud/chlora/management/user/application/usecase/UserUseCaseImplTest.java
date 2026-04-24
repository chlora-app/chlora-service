package cloud.chlora.management.user.application.usecase;

import cloud.chlora.management.shared.error.UserErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import cloud.chlora.management.user.adapter.in.web.request.UserCreateRequest;
import cloud.chlora.management.user.adapter.in.web.request.UserUpdateRequest;
import cloud.chlora.management.user.adapter.in.web.response.*;
import cloud.chlora.management.user.domain.model.User;
import cloud.chlora.management.user.domain.model.UserRole;
import cloud.chlora.management.user.domain.port.UserReadRepository;
import cloud.chlora.management.user.domain.port.UserWriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseImplTest {

    @Mock
    private UserReadRepository readRepository;

    @Mock
    private UserWriteRepository writeRepository;

    @InjectMocks
    private UserUseCaseImpl userUseCase;

    private User activeUser;
    private User deletedUser;

    @BeforeEach
    void setUp() {
        activeUser = new User(1L, "user-1", "test@test.com", "pass", "Test User", UserRole.USER, Instant.now(), Instant.now(), null);
        deletedUser = new User(2L, "user-2", "deleted@test.com", "pass", "Deleted User", UserRole.USER, Instant.now(), Instant.now(), Instant.now());
    }

    @Test
    @DisplayName("findAllActive should return paged response with correct mapping and pagination")
    void findAllActive_shouldReturnPagedResponse() {
        // Arrange
        when(readRepository.findAllActive(anyString(), any(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(activeUser));
        when(readRepository.countActive(anyString(), any())).thenReturn(1L);

        // Act
        PagedUserResponse<UserGetResponse> response = userUseCase.findAllActive(1, 10, "search", "name", "desc", UserRole.USER);

        // Assert
        assertThat(response.totalElements()).isEqualTo(1L);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.content()).hasSize(1);
        
        UserGetResponse item = response.content().getFirst();
        assertThat(item.userId()).isEqualTo(activeUser.userId());
        assertThat(item.email()).isEqualTo(activeUser.email());
        
        verify(readRepository).findAllActive("search", UserRole.USER, "name", "DESC", 10, 0);
    }

    @Test
    @DisplayName("findAllDeleted should return paged response with deleted users")
    void findAllDeleted_shouldReturnPagedResponse() {
        // Arrange
        when(readRepository.findAllDeleted(anyString(), any(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(deletedUser));
        when(readRepository.countDeleted(anyString(), any())).thenReturn(1L);

        // Act
        PagedUserResponse<UserDeletedResponse> response = userUseCase.findAllDeleted(1, 10, "search", "email", "asc", UserRole.ADMIN);

        // Assert
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().userId()).isEqualTo(deletedUser.userId());
        assertThat(response.content().getFirst().deletedAt()).isEqualTo(deletedUser.deletedAt());
        
        verify(readRepository).findAllDeleted("search", UserRole.ADMIN, "email", "ASC", 10, 0);
    }

    @Test
    @DisplayName("findByUserId should return user if active")
    void findByUserId_shouldReturnUser_whenActive() {
        // Arrange
        when(readRepository.findByUserId("user-1")).thenReturn(Optional.of(activeUser));

        // Act
        UserGetResponse response = userUseCase.findByUserId("user-1");

        // Assert
        assertThat(response.userId()).isEqualTo("user-1");
    }

    @Test
    @DisplayName("findByUserId should throw exception if user not found")
    void findByUserId_shouldThrowNotFound_whenNotFound() {
        // Arrange
        when(readRepository.findByUserId("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userUseCase.findByUserId("unknown"))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND));
    }

    @Test
    @DisplayName("findByUserId should throw exception if user is deleted")
    void findByUserId_shouldThrowDeleted_whenDeleted() {
        // Arrange
        when(readRepository.findByUserId("user-2")).thenReturn(Optional.of(deletedUser));

        // Act & Assert
        assertThatThrownBy(() -> userUseCase.findByUserId("user-2"))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(UserErrorCode.USER_ALREADY_DELETED));
    }

    @Test
    @DisplayName("createUser should call write repository and return response")
    void createUser_shouldReturnResponse() {
        // Arrange
        UserCreateRequest request = new UserCreateRequest("new@test.com", "New User", UserRole.USER);
        when(writeRepository.create(request)).thenReturn(activeUser);

        // Act
        UserCreateResponse response = userUseCase.createUser(request);

        // Assert
        assertThat(response.email()).isEqualTo(activeUser.email());
        verify(writeRepository).create(request);
    }

    @Test
    @DisplayName("updateUser should throw exception if request is empty")
    void updateUser_shouldThrowException_whenEmptyPatch() {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest(null, null, null);

        // Act & Assert
        assertThatThrownBy(() -> userUseCase.updateUser("user-1", request))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(UserErrorCode.USER_PATCH_EMPTY));
    }

    @Test
    @DisplayName("updateUser should call write repository if request is not empty")
    void updateUser_shouldCallRepository_whenNotEmpty() {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest("updated@test.com", null, null);
        when(writeRepository.update(eq("user-1"), eq(request))).thenReturn(activeUser);

        // Act
        UserUpdateResponse response = userUseCase.updateUser("user-1", request);

        // Assert
        assertThat(response.userId()).isEqualTo(activeUser.userId());
        verify(writeRepository).update("user-1", request);
    }

    @Test
    @DisplayName("deleteUser should call softDelete if user is active")
    void deleteUser_shouldCallSoftDelete() {
        // Arrange
        when(readRepository.findByUserId("user-1")).thenReturn(Optional.of(activeUser));

        // Act
        userUseCase.deleteUser("user-1");

        // Assert
        verify(writeRepository).softDelete("user-1");
    }

    @Test
    @DisplayName("deleteUser should throw exception if user is already deleted")
    void deleteUser_shouldThrowException_whenAlreadyDeleted() {
        // Arrange
        when(readRepository.findByUserId("user-2")).thenReturn(Optional.of(deletedUser));

        // Act & Assert
        assertThatThrownBy(() -> userUseCase.deleteUser("user-2"))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(UserErrorCode.USER_ALREADY_DELETED));
    }

    @Test
    @DisplayName("restoreUser should call restore if user is deleted")
    void restoreUser_shouldCallRestore() {
        // Arrange
        when(readRepository.findByUserId("user-2")).thenReturn(Optional.of(deletedUser));

        // Act
        userUseCase.restoreUser("user-2");

        // Assert
        verify(writeRepository).restore("user-2");
    }

    @Test
    @DisplayName("restoreUser should throw exception if user is active")
    void restoreUser_shouldThrowException_whenActive() {
        // Arrange
        when(readRepository.findByUserId("user-1")).thenReturn(Optional.of(activeUser));

        // Act & Assert
        assertThatThrownBy(() -> userUseCase.restoreUser("user-1"))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(UserErrorCode.USER_IS_ACTIVE));
    }
}
