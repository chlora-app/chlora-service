package cloud.chlora.management.device.application.usecase;

import cloud.chlora.management.device.adapter.in.web.request.DeviceCreateRequest;
import cloud.chlora.management.device.adapter.in.web.request.DeviceUpdateRequest;
import cloud.chlora.management.device.adapter.in.web.response.*;
import cloud.chlora.management.device.domain.model.Device;
import cloud.chlora.management.device.domain.model.DeviceStatus;
import cloud.chlora.management.device.domain.port.DeviceReadRepository;
import cloud.chlora.management.device.domain.port.DeviceWriteRepository;
import cloud.chlora.management.device.domain.port.PotExistencePort;
import cloud.chlora.management.device.domain.port.PotNamePort;
import cloud.chlora.management.shared.error.IotErrorCode;
import cloud.chlora.management.shared.exception.AppException;
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
class DeviceUseCaseImplTest {

    @Mock
    private DeviceReadRepository readRepository;

    @Mock
    private DeviceWriteRepository writeRepository;

    @Mock
    private PotExistencePort potExistencePort;

    @Mock
    private PotNamePort potNamePort;

    @InjectMocks
    private DeviceUseCaseImpl deviceUseCase;

    private Device activeDevice;

    @BeforeEach
    void setUp() {
        activeDevice = Device.builder()
                .id(1L)
                .deviceId("dev-1")
                .deviceName("Sensor A")
                .status(DeviceStatus.ONLINE)
                .potId("pot-1")
                .potName("Living Room")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("findAll should return paged response with filtered items")
    void findAll_shouldReturnPagedResponse() {
        // Arrange
        when(readRepository.findAll(anyString(), anyString(), any(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(activeDevice));
        when(readRepository.countAll(anyString(), anyString(), any())).thenReturn(1L);

        // Act
        PagedDeviceResponse response = deviceUseCase.findAll(1, 10, "search", "deviceName", "desc", "pot-1", "ONLINE");

        // Assert
        assertThat(response.totalElements()).isEqualTo(1L);
        assertThat(response.devices()).hasSize(1);
        assertThat(response.devices().getFirst().deviceName()).isEqualTo("Sensor A");
        
        verify(readRepository).findAll("search", "pot-1", DeviceStatus.ONLINE, "deviceName", "DESC", 10, 0);
    }

    @Test
    @DisplayName("findByDeviceId should return device and fetch pot name")
    void findByDeviceId_shouldReturnDevice() {
        // Arrange
        when(readRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(activeDevice));
        when(potNamePort.getPotName("pot-1")).thenReturn("Living Room Name");

        // Act
        DeviceGetResponse response = deviceUseCase.findByDeviceId("dev-1");

        // Assert
        assertThat(response.deviceId()).isEqualTo("dev-1");
        assertThat(response.potName()).isEqualTo("Living Room Name");
    }

    @Test
    @DisplayName("createDevice should successfully create device if pot exists")
    void createDevice_shouldReturnResponse_whenPotExists() {
        // Arrange
        DeviceCreateRequest request = new DeviceCreateRequest("Sensor A", "pot-1");
        when(potExistencePort.existsByPotId("pot-1")).thenReturn(true);
        when(writeRepository.create(request)).thenReturn(activeDevice);

        // Act
        DeviceCreateResponse response = deviceUseCase.createDevice(request);

        // Assert
        assertThat(response.deviceId()).isEqualTo("dev-1");
        verify(writeRepository).create(request);
    }

    @Test
    @DisplayName("createDevice should throw exception if pot does not exist")
    void createDevice_shouldThrowException_whenPotNotFound() {
        // Arrange
        DeviceCreateRequest request = new DeviceCreateRequest("Sensor A", "pot-1");
        when(potExistencePort.existsByPotId("pot-1")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> deviceUseCase.createDevice(request))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(IotErrorCode.POT_NOT_FOUND));
    }

    @Test
    @DisplayName("updateDevice should throw exception if status is invalid")
    void updateDevice_shouldThrowException_whenStatusInvalid() {
        // Arrange
        DeviceUpdateRequest request = new DeviceUpdateRequest("New Name", "INVALID_STATUS", "pot-1");
        when(potExistencePort.existsByPotId("pot-1")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> deviceUseCase.updateDevice("dev-1", request))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(IotErrorCode.DEVICE_STATUS_INVALID));
    }

    @Test
    @DisplayName("updateDevice should successfully update if all valid")
    void updateDevice_shouldReturnResponse() {
        // Arrange
        DeviceUpdateRequest request = new DeviceUpdateRequest("New Name", "OFFLINE", "pot-1");
        when(potExistencePort.existsByPotId("pot-1")).thenReturn(true);
        when(readRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(activeDevice));
        
        Device updated = Device.builder()
                .deviceId("dev-1")
                .deviceName("New Name")
                .status(DeviceStatus.OFFLINE)
                .potId("pot-1")
                .updatedAt(Instant.now())
                .build();
        when(writeRepository.update(eq("dev-1"), eq(request))).thenReturn(updated);

        // Act
        DeviceUpdateResponse response = deviceUseCase.updateDevice("dev-1", request);

        // Assert
        assertThat(response.deviceName()).isEqualTo("New Name");
        assertThat(response.status()).isEqualTo(DeviceStatus.OFFLINE);
    }

    @Test
    @DisplayName("deleteDevice should call softDelete")
    void deleteDevice_shouldCallSoftDelete() {
        // Arrange
        when(readRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(activeDevice));

        // Act
        deviceUseCase.deleteDevice("dev-1");

        // Assert
        verify(writeRepository).softDelete("dev-1");
    }
}
