package cloud.chlora.management.device.application.usecase;

import cloud.chlora.management.device.domain.port.PotNamePort;
import cloud.chlora.management.shared.error.IotErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import cloud.chlora.management.device.adapter.in.web.request.DeviceCreateRequest;
import cloud.chlora.management.device.adapter.in.web.request.DeviceUpdateRequest;
import cloud.chlora.management.device.adapter.in.web.response.*;
import cloud.chlora.management.device.application.port.in.DeviceUseCase;
import cloud.chlora.management.device.domain.model.Device;
import cloud.chlora.management.device.domain.model.DeviceStatus;
import cloud.chlora.management.device.domain.port.PotExistencePort;
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

    private final DeviceReadRepository readRepository;
    private final DeviceWriteRepository writeRepository;
    private final PotExistencePort potExistencePort;
    private final PotNamePort potNamePort;

    // ── Queries ───────────────────────────────────────────────────────────────
    @Override
    public PagedDeviceResponse findAll(
            int page, int size, String search, String sort, String order,
            String potId, String status
    ) {
        if (page < 1) throw AppException.of(IotErrorCode.PAGE_LOWER_THAN_ONE);
        if (size < 1) throw AppException.of(IotErrorCode.SIZE_LOWER_THAN_ONE);

        DeviceStatus parsedStatus = parseStatus(status);
        int offset = (page - 1) * size;

        List<Device> rawDevices = readRepository
                .findAll(search, potId, parsedStatus, resolveColumn(sort), resolveDir(order), size, offset);

        List<PagedDeviceItem> devices = rawDevices.stream()
                .map(d -> new PagedDeviceItem(
                        d.deviceId(), d.deviceName(), d.status(),
                        d.potId(), d.potName(), d.createdAt()
                ))
                .toList();

        long total      = readRepository.countAll(search, potId, parsedStatus);
        int  totalPages = (int) Math.ceil((double) total / size);

        return new PagedDeviceResponse(total, page, size, totalPages, devices);
    }

    @Override
    public DeviceGetResponse findByDeviceId(String deviceId) {
        Device device = requireDevice(deviceId);
        if (device.isDeleted()) throw AppException.of(IotErrorCode.DEVICE_ALREADY_DELETED);

        String potName = potNamePort.getPotName(device.potId());
        log.info("[DeviceUseCase] Pot Name: {}", potName);

        return new DeviceGetResponse(
                device.deviceId(), device.deviceName(), device.potId(),
                potName, device.status(), device.createdAt(), device.updatedAt()
        );
    }

    // ── Commands ──────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public DeviceCreateResponse createDevice(DeviceCreateRequest request) {
        if (!potExistencePort.existsByPotId(request.potId())) {
            log.warn("[DeviceUseCase] createDevice - pot not found: {}", request.potId());
            throw AppException.of(IotErrorCode.POT_NOT_FOUND);
        }

        Device saved = writeRepository.create(request);
        log.info("[DeviceUseCase] created deviceId={}", saved.deviceId());

        return new DeviceCreateResponse(
                saved.deviceId(), saved.deviceName(), saved.potId(), saved.status(), saved.createdAt()
        );
    }

    @Override
    @Transactional
    public DeviceUpdateResponse updateDevice(String deviceId, DeviceUpdateRequest request) {
        if (request.deviceName() == null && request.status() == null && request.potId() == null) {
            throw AppException.of(IotErrorCode.DEVICE_UPDATE_EMPTY);
        }

        if (request.deviceName() != null && request.deviceName().isBlank()) {
            throw AppException.of(IotErrorCode.DEVICE_REQUEST_INVALID);
        }

        String trimmedPotId    = request.potId() != null ? request.potId().trim() : null;
        boolean isUnassigning  = trimmedPotId != null && trimmedPotId.equalsIgnoreCase("UNASSIGNED");
        String normalizedPotId = isUnassigning ? "UNASSIGNED" : trimmedPotId;

        if (normalizedPotId != null && !isUnassigning && !potExistencePort.existsByPotId(normalizedPotId)) {
            log.warn("[DeviceUseCase] updateDevice - pot not found: {}", request.potId());
            throw AppException.of(IotErrorCode.POT_NOT_FOUND);
        }

        if (normalizedPotId != null && !isUnassigning) {
            readRepository.findByPotId(normalizedPotId).ifPresent(existing -> {
                if (!existing.deviceId().equals(deviceId)) {
                    log.warn("[DeviceUseCase] updateDevice - pot {} already assigned to device {}", normalizedPotId, existing.deviceId());
                    throw AppException.of(IotErrorCode.POT_ALREADY_ASSIGNED);
                }
            });
        }
        if (request.status() != null) parseStatus(request.status());

        Device device = requireDevice(deviceId);
        if (device.isDeleted()) throw AppException.of(IotErrorCode.DEVICE_ALREADY_DELETED);

        DeviceUpdateRequest normalized = new DeviceUpdateRequest(
                request.deviceName(),
                request.status(),
                normalizedPotId
        );

        Device updated = writeRepository.update(deviceId, normalized);
        log.info("[DeviceUseCase] updated deviceId={}", deviceId);

        return new DeviceUpdateResponse(
                updated.deviceId(), updated.deviceName(), updated.status(), updated.potId(), updated.updatedAt()
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
            case "deviceId",   "device_id"   -> "deviceId";
            case "deviceName", "device_name" -> "deviceName";
            case "status"                    -> "status";
            case "potId",      "pot_id"      -> "potId";
            case "potName",    "pot_name"    -> "potName";
            default                          -> "createdAt";
        };
    }

    private String resolveDir(String order) {
        return "desc".equalsIgnoreCase(order) ? "DESC" : "ASC";
    }
}