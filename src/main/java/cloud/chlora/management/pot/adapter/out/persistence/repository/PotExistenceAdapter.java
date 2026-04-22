package cloud.chlora.management.pot.adapter.out.persistence.repository;

import cloud.chlora.management.device.domain.port.PotExistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PotExistenceAdapter implements PotExistencePort {

    private final PotReadJpaRepository repository;

    @Override
    public boolean existsByPotId(String potId) {
        return repository.existsByPotIdActive(potId);
    }
}