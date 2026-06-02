package cloud.chlora.management.pot.adapter.out.persistence.repository;

import cloud.chlora.management.pot.adapter.out.persistence.mapper.PotPersistenceMapper;
import cloud.chlora.management.pot.domain.model.Pot;
import cloud.chlora.management.pot.domain.model.PotSummary;
import cloud.chlora.management.pot.domain.port.PotReadRepository;
import cloud.chlora.management.device.adapter.out.persistence.mapper.DevicePersistenceMapper;
import cloud.chlora.management.device.adapter.out.persistence.repository.DeviceReadJpaRepository;
import cloud.chlora.management.device.domain.model.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PotReadRepositoryAdapter implements PotReadRepository {

    private final PotReadJpaRepository potRepo;
    private final DeviceReadJpaRepository deviceRepo;

    @Override
    public Optional<Pot> findByPotId(String potId) {
        return potRepo.findByPotId(potId)
                .map(PotPersistenceMapper::toDomain);
    }

    @Override
    public List<PotSummary> findAllExisting(
            String search,
            String sortColumn, String sortDirection,
            int limit, int offset
    ) {
        var pageable = buildPageable(limit, offset, sortColumn, sortDirection);
        return potRepo.findAllExisting(normalize(search), pageable)
                .map(p -> new PotSummary(p.getPotId(), p.getPotName(), p.getIsMonitored()))
                .toList();
    }

    @Override
    public long countExisting(String search) {
        return potRepo.findAllExisting(normalize(search), PageRequest.of(0, 1))
                .getTotalElements();
    }

    @Override
    public List<Pot> findAllAsList() {
        return potRepo.findAllAsList()
                .stream()
                .map(PotPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Device> findDevicesByPotId(String potId) {
        return deviceRepo.findAllByPotIdActive(potId)
                .stream()
                .map(DevicePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByPotName(String potName) {
        return potRepo.existsByPotNameActive(potName);
    }

    @Override
    public boolean existsByPotId(String potId) {
        return potRepo.existsByPotIdActive(potId);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private PageRequest buildPageable(int limit, int offset, String col, String dir) {
        var direction = "DESC".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(offset / limit, limit, Sort.by(direction, toColumnName(col)));
    }

    private String toColumnName(String col) {
        return switch (col) {
            case "potId"   -> "pot_id";
            case "potName" -> "pot_name";
            case "createdAt" -> "created_at";
            default          -> "created_at";
        };
    }

    private String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}