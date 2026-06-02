package cloud.chlora.management.device.domain.port;

import cloud.chlora.management.device.adapter.in.web.request.DeviceCreateRequest;
import cloud.chlora.management.device.adapter.in.web.request.DeviceUpdateRequest;
import cloud.chlora.management.device.domain.model.Device;

import java.time.Instant;

/**
 * Outbound port — write operations on the devices table.
 */
public interface DeviceWriteRepository {

    Device create(DeviceCreateRequest request);

    Device update(String deviceId, DeviceUpdateRequest request);

    void softDelete(String deviceId);

    int setOfflineIfStale(Instant threshold);
}