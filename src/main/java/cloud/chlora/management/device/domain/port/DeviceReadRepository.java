package cloud.chlora.management.device.domain.port;

import cloud.chlora.management.device.domain.model.Device;
import cloud.chlora.management.device.domain.model.DeviceStatus;

import java.util.List;
import java.util.Optional;

public interface DeviceReadRepository {

    Optional<Device> findByDeviceId(String deviceId);

    List<Device> findAll(
            String search, String potId, DeviceStatus status,
            String sortColumn, String sortDirection,
            int limit, int offset
    );

    long countAll(String search, String potId, DeviceStatus status);
}