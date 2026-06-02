package cloud.chlora.management.pot.adapter.out.persistence.repository;


import cloud.chlora.management.device.domain.port.PotNamePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PotNameAdapter implements PotNamePort {

    private final PotReadJpaRepository repository;

    @Override
    public String getPotName(String potId) {
        return repository.getPotName(potId);
    }

    @Override
    public Map<String, String> getPotNames(Set<String> potIds) {
        if (potIds == null || potIds.isEmpty()) return Map.of();
        return repository.getPotNames(potIds)
                .stream()
                .collect(Collectors.toMap(
                        PotReadJpaRepository.PotIdNameProjection::getPotId,
                        PotReadJpaRepository.PotIdNameProjection::getPotName
                ));
    }
}