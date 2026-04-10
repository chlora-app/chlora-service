package cloud.chlora.management.device.application.usecase;

import cloud.chlora.management.shared.error.IotErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import cloud.chlora.management.device.adapter.in.web.request.DeviceCreateRequest;
import cloud.chlora.management.device.adapter.in.web.request.DeviceUpdateRequest;
import cloud.chlora.management.device.adapter.in.web.response.*;
import cloud.chlora.management.device.application.port.in.DeviceUseCase;
import cloud.chlora.management.device.domain.model.Device;
import cloud.chlora.management.device.domain.model.DeviceStatus;
import cloud.chlora.management.device.domain.port.ClusterExistencePort;
import cloud.chlora.management.device.domain.port.DeviceReadRepository;
import cloud.chlora.management.device.domain.port.DeviceWriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceUseCaseImpl implements DeviceUseCase {

    private final DeviceReadRepository  readRepository;
    private final DeviceWriteRepository writeRepository;
    private final ClusterExistencePort  clusterExistencePort;

    // ── Queries ───────────────────────────────────────────────────────────────

    @Override
    public PagedDeviceResponse findAll(
            int page, int size, String search, String sort, String order,
            String clusterId, String status
    ) {
        if (page < 1) throw AppException.of(IotErrorCode.PAGE_LOWER_THAN_ONE);
        if (size < 1) throw AppException.of(IotErrorCode.SIZE_LOWER_THAN_ONE);

        DeviceStatus parsedStatus = parseStatus(status);
        int offset = (page - 1) * size;

        List<PagedDeviceItem> devices = readRepository
                .findAll(search, clusterId, parsedStatus, resolveColumn(sort), resolveDir(order), size, offset)
                .stream()
                .map(d -> new PagedDeviceItem(
                        d.deviceId(), d.deviceName(), d.deviceType(),
                        d.status(), d.clusterId(), null, d.createdAt()
                ))
                .toList();

        long total      = readRepository.countAll(search, clusterId, parsedStatus);
        int  totalPages = (int) Math.ceil((double) total / size);

        return new PagedDeviceResponse(total, page, size, totalPages, devices);
    }

    @Override
    public DeviceGetResponse findByDeviceId(String deviceId) {
        Device device = requireDevice(deviceId);
        if (device.isDeleted()) throw AppException.of(IotErrorCode.DEVICE_ALREADY_DELETED);

        return new DeviceGetResponse(
                device.deviceId(), device.deviceName(), device.deviceType(),
                device.clusterId(), device.status(), device.createdAt(), device.updatedAt()
        );
    }

    // ── Commands ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public DeviceCreateResponse createDevice(DeviceCreateRequest request) {
        if (!clusterExistencePort.existsByClusterId(request.clusterId())) {
            log.warn("[DeviceUseCase] createDevice - cluster not found: {}", request.clusterId());
            throw AppException.of(IotErrorCode.CLUSTER_NOT_FOUND);
        }

        Device saved = writeRepository.create(request);
        log.info("[DeviceUseCase] created deviceId={}", saved.deviceId());

        return new DeviceCreateResponse(
                saved.deviceId(), saved.deviceName(), saved.deviceType(),
                saved.clusterId(), saved.status(), saved.createdAt()
        );
    }

    @Override
    @Transactional
    public DeviceUpdateResponse updateDevice(String deviceId, DeviceUpdateRequest request) {
        if (request.deviceName() == null && request.deviceType() == null
                && request.status() == null && request.clusterId() == null) {
            throw AppException.of(IotErrorCode.DEVICE_UPDATE_EMPTY);
        }

        if (request.deviceName() != null && request.deviceName().isBlank()) {
            throw AppException.of(IotErrorCode.DEVICE_REQUEST_INVALID);
        }

        if (request.clusterId() != null && !clusterExistencePort.existsByClusterId(request.clusterId())) {
            log.warn("[DeviceUseCase] updateDevice - cluster not found: {}", request.clusterId());
            throw AppException.of(IotErrorCode.CLUSTER_NOT_FOUND);
        }

        // validate status string before hitting the write adapter
        if (request.status() != null) parseStatus(request.status());

        Device device = requireDevice(deviceId);
        if (device.isDeleted()) throw AppException.of(IotErrorCode.DEVICE_ALREADY_DELETED);

        Device updated = writeRepository.update(deviceId, request);
        log.info("[DeviceUseCase] updated deviceId={}", deviceId);

        return new DeviceUpdateResponse(
                updated.deviceId(), updated.deviceName(), updated.deviceType(),
                updated.status(), updated.clusterId(), updated.updatedAt()
        );
    }

    @Override
    @Transactional
    public void deleteDevice(String deviceId) {
        Device device = requireDevice(deviceId);
        if (device.isDeleted()) throw AppException.of(IotErrorCode.DEVICE_ALREADY_DELETED);

        writeRepository.softDelete(deviceId);
        log.info("[DeviceUseCase] deleted deviceId={}", deviceId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Device requireDevice(String deviceId) {
        return readRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> AppException.of(IotErrorCode.DEVICE_NOT_FOUND));
    }

    private DeviceStatus parseStatus(String status) {
        if (status == null || status.isBlank()) return null;
        try {
            return DeviceStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw AppException.of(IotErrorCode.DEVICE_STATUS_INVALID);
        }
    }

    private String resolveColumn(String sort) {
        return switch (sort == null ? "" : sort) {
            case "deviceId", "device_id"       -> "deviceId";
            case "deviceName", "device_name"   -> "deviceName";
            case "deviceType", "device_type"   -> "deviceType";
            case "status"                      -> "status";
            case "clusterId", "cluster_id"     -> "clusterId";
            default                            -> "createdAt";
        };
    }

    private String resolveDir(String order) {
        return "desc".equalsIgnoreCase(order) ? "DESC" : "ASC";
    }
}