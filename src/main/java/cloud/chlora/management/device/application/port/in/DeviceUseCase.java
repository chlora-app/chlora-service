package cloud.chlora.management.device.application.port.in;

import cloud.chlora.management.device.adapter.in.web.request.DeviceCreateRequest;
import cloud.chlora.management.device.adapter.in.web.request.DeviceUpdateRequest;
import cloud.chlora.management.device.adapter.in.web.response.*;

public interface DeviceUseCase {

    PagedDeviceResponse findAll(
            int page, int size, String search, String sort, String order,
            String clusterId, String status
    );

    DeviceGetResponse findByDeviceId(String deviceId);

    DeviceCreateResponse createDevice(DeviceCreateRequest request);

    DeviceUpdateResponse updateDevice(String deviceId, DeviceUpdateRequest request);

    void deleteDevice(String deviceId);
}