package cloud.chlora.management.pot.application.usecase;

import cloud.chlora.management.pot.adapter.in.web.request.PotCreateRequest;
import cloud.chlora.management.pot.adapter.in.web.request.PotUpdateRequest;
import cloud.chlora.management.pot.adapter.in.web.response.*;
import cloud.chlora.management.pot.application.port.in.PotUseCase;
import cloud.chlora.management.pot.domain.model.Pot;
import cloud.chlora.management.pot.domain.port.PotReadRepository;
import cloud.chlora.management.pot.domain.port.PotWriteRepository;
import cloud.chlora.management.shared.error.IotErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import cloud.chlora.management.device.adapter.in.web.response.DeviceListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PotUseCaseImpl implements PotUseCase {

    private final PotReadRepository  readRepository;
    private final PotWriteRepository writeRepository;

    // ── Queries ───────────────────────────────────────────────────────────────

    @Override
    public PagedPotResponse findAll(
            int page, int size, String search, String sort, String order
    ) {
        if (page < 1) throw AppException.of(IotErrorCode.PAGE_LOWER_THAN_ONE);
        if (size < 1) throw AppException.of(IotErrorCode.SIZE_LOWER_THAN_ONE);

        int offset = (page - 1) * size;

        List<PotSummaryResponse> pots = readRepository
                .findAllExisting(search, resolveColumn(sort), resolveDir(order), size, offset)
                .stream()
                .map(p -> new PotSummaryResponse(p.potId(), p.potName(), p.isMonitored()))
                .toList();

        long total      = readRepository.countExisting(search);
        int  totalPages = (int) Math.ceil((double) total / size);

        return new PagedPotResponse(total, page, size, totalPages, pots);
    }

    @Override
    public PotGetResponse findByPotId(String potId) {
        Pot pot = requirePot(potId);
        if (pot.isDeleted()) throw AppException.of(IotErrorCode.POT_ALREADY_DELETED);

        List<DeviceListResponse> devices = readRepository.findDevicesByPotId(potId)
                .stream()
                .map(d -> new DeviceListResponse(d.deviceId(), d.deviceName(), d.status()))
                .toList();

        return new PotGetResponse(pot.potId(), pot.potName(), devices.size(), devices);
    }

    @Override
    public PotListResponse getPotList() {
        List<PotListResponse.PotInfo> list = readRepository.findAllAsList()
                .stream()
                .map(p -> new PotListResponse.PotInfo(p.potName(), p.potId()))
                .toList();
        return new PotListResponse(list);
    }

    // ── Commands ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public PotCreateResponse createPot(PotCreateRequest request) {
        if (readRepository.existsByPotName(request.potName())) {
            log.warn("[PotUseCase] createPot - name exists: {}", request.potName());
            throw AppException.of(IotErrorCode.POT_NAME_ALREADY_EXISTS);
        }

        Pot saved = writeRepository.create(request);
        log.info("[PotUseCase] created potId={}", saved.potId());
        return new PotCreateResponse(saved.potId(), saved.potName(), saved.createdAt());
    }

    @Override
    @Transactional
    public PotUpdateResponse updatePot(String potId, PotUpdateRequest request) {
        if (request.potName() == null || request.potName().isBlank()) {
            throw AppException.of(IotErrorCode.POT_UPDATE_EMPTY);
        }

        Pot existing = requirePot(potId);

        if (existing.potName().equals(request.potName())) {
            return new PotUpdateResponse(existing.potId(), existing.potName(), existing.updatedAt());
        }

        if (readRepository.existsByPotName(request.potName())) {
            log.warn("[PotUseCase] updatePot - name exists: {}", request.potName());
            throw AppException.of(IotErrorCode.POT_NAME_ALREADY_EXISTS);
        }

        Pot updated = writeRepository.update(potId, request);
        log.info("[PotUseCase] updated potId={}", potId);
        return new PotUpdateResponse(updated.potId(), updated.potName(), updated.updatedAt());
    }

    @Override
    @Transactional
    public void deletePot(String potId) {
        Pot pot = requirePot(potId);
        if (pot.isDeleted()) throw AppException.of(IotErrorCode.POT_ALREADY_DELETED);

        writeRepository.softDelete(potId);
        writeRepository.softDeleteDevicesByPotId(potId);
        log.info("[PotUseCase] deleted potId={}", potId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Pot requirePot(String potId) {
        return readRepository.findByPotId(potId)
                .orElseThrow(() -> AppException.of(IotErrorCode.POT_NOT_FOUND));
    }

    private String resolveColumn(String sort) {
        return switch (sort == null ? "" : sort) {
            case "potId",   "pot_id"   -> "potId";
            case "potName", "pot_name" -> "potName";
            default                    -> "createdAt";
        };
    }

    private String resolveDir(String order) {
        return "desc".equalsIgnoreCase(order) ? "DESC" : "ASC";
    }
}