package cloud.chlora.management.pot.application.usecase;

import cloud.chlora.management.pot.adapter.in.web.request.PotCreateRequest;
import cloud.chlora.management.pot.adapter.in.web.request.PotUpdateRequest;
import cloud.chlora.management.pot.adapter.in.web.response.*;
import cloud.chlora.management.pot.domain.model.Pot;
import cloud.chlora.management.pot.domain.model.PotSummary;
import cloud.chlora.management.pot.domain.port.PotReadRepository;
import cloud.chlora.management.pot.domain.port.PotWriteRepository;
import cloud.chlora.management.shared.error.IotErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import cloud.chlora.management.device.domain.model.Device;
import cloud.chlora.management.device.domain.model.DeviceStatus;
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
class PotUseCaseImplTest {

    @Mock
    private PotReadRepository readRepository;

    @Mock
    private PotWriteRepository writeRepository;

    @InjectMocks
    private PotUseCaseImpl potUseCase;

    private Pot activePot;
    private Pot deletedPot;

    @BeforeEach
    void setUp() {
        activePot = new Pot(1L, "pot-1", "Living Room", Instant.now(), Instant.now(), null);
        deletedPot = new Pot(2L, "pot-2", "Kitchen", Instant.now(), Instant.now(), Instant.now());
    }

    @Test
    @DisplayName("findAll should return paged response with mapped summaries")
    void findAll_shouldReturnPagedResponse() {
        // Arrange
        PotSummary summary = new PotSummary("pot-1", "Living Room", true);
        when(readRepository.findAllExisting(anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(summary));
        when(readRepository.countExisting(anyString())).thenReturn(1L);

        // Act
        PagedPotResponse response = potUseCase.findAll(1, 10, "search", "potName", "asc");

        // Assert
        assertThat(response.totalElements()).isEqualTo(1L);
        assertThat(response.pots()).hasSize(1);
        assertThat(response.pots().getFirst().potName()).isEqualTo("Living Room");
        assertThat(response.pots().getFirst().isMonitored()).isTrue();
        
        verify(readRepository).findAllExisting("search", "potName", "ASC", 10, 0);
    }

    @Test
    @DisplayName("findAll should throw exception if page or size is less than 1")
    void findAll_shouldThrowException_whenInvalidPagination() {
        assertThatThrownBy(() -> potUseCase.findAll(0, 10, null, null, null))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(IotErrorCode.PAGE_LOWER_THAN_ONE));

        assertThatThrownBy(() -> potUseCase.findAll(1, 0, null, null, null))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(IotErrorCode.SIZE_LOWER_THAN_ONE));
    }

    @Test
    @DisplayName("findByPotId should return pot with its devices")
    void findByPotId_shouldReturnPotWithDevices() {
        // Arrange
        Device device = new Device(1L, "dev-1", "Sensor A", DeviceStatus.ONLINE, "pot-1", "Living Room", Instant.now(), Instant.now(), null);
        when(readRepository.findByPotId("pot-1")).thenReturn(Optional.of(activePot));
        when(readRepository.findDevicesByPotId("pot-1")).thenReturn(List.of(device));

        // Act
        PotGetResponse response = potUseCase.findByPotId("pot-1");

        // Assert
        assertThat(response.potId()).isEqualTo("pot-1");
        assertThat(response.totalDevices()).isEqualTo(1);
        assertThat(response.devices()).hasSize(1);
        assertThat(response.devices().getFirst().deviceId()).isEqualTo("dev-1");
    }

    @Test
    @DisplayName("findByPotId should throw exception if pot is deleted")
    void findByPotId_shouldThrowException_whenDeleted() {
        // Arrange
        when(readRepository.findByPotId("pot-2")).thenReturn(Optional.of(deletedPot));

        // Act & Assert
        assertThatThrownBy(() -> potUseCase.findByPotId("pot-2"))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(IotErrorCode.POT_ALREADY_DELETED));
    }

    @Test
    @DisplayName("createPot should call write repository and return response")
    void createPot_shouldReturnResponse() {
        // Arrange
        PotCreateRequest request = new PotCreateRequest("Living Room");
        when(readRepository.existsByPotName("Living Room")).thenReturn(false);
        when(writeRepository.create(request)).thenReturn(activePot);

        // Act
        PotCreateResponse response = potUseCase.createPot(request);

        // Assert
        assertThat(response.potName()).isEqualTo("Living Room");
        verify(writeRepository).create(request);
    }

    @Test
    @DisplayName("createPot should throw exception if name already exists")
    void createPot_shouldThrowException_whenNameExists() {
        // Arrange
        PotCreateRequest request = new PotCreateRequest("Living Room");
        when(readRepository.existsByPotName("Living Room")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> potUseCase.createPot(request))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(IotErrorCode.POT_NAME_ALREADY_EXISTS));
    }

    @Test
    @DisplayName("updatePot should call write repository if name is changed and valid")
    void updatePot_shouldCallRepository() {
        // Arrange
        PotUpdateRequest request = new PotUpdateRequest("New Name");
        when(readRepository.findByPotId("pot-1")).thenReturn(Optional.of(activePot));
        when(readRepository.existsByPotName("New Name")).thenReturn(false);
        
        Pot updatedPot = new Pot(1L, "pot-1", "New Name", Instant.now(), Instant.now(), null);
        when(writeRepository.update(eq("pot-1"), eq(request))).thenReturn(updatedPot);

        // Act
        PotUpdateResponse response = potUseCase.updatePot("pot-1", request);

        // Assert
        assertThat(response.potName()).isEqualTo("New Name");
        verify(writeRepository).update("pot-1", request);
    }

    @Test
    @DisplayName("updatePot should return existing if name is same")
    void updatePot_shouldReturnExisting_whenNameSame() {
        // Arrange
        PotUpdateRequest request = new PotUpdateRequest("Living Room");
        when(readRepository.findByPotId("pot-1")).thenReturn(Optional.of(activePot));

        // Act
        PotUpdateResponse response = potUseCase.updatePot("pot-1", request);

        // Assert
        assertThat(response.potName()).isEqualTo("Living Room");
        verify(writeRepository, never()).update(anyString(), any());
    }

    @Test
    @DisplayName("deletePot should call softDelete on pot and devices")
    void deletePot_shouldCallSoftDelete() {
        // Arrange
        when(readRepository.findByPotId("pot-1")).thenReturn(Optional.of(activePot));

        // Act
        potUseCase.deletePot("pot-1");

        // Assert
        verify(writeRepository).softDelete("pot-1");
        verify(writeRepository).softDeleteDevicesByPotId("pot-1");
    }
}
