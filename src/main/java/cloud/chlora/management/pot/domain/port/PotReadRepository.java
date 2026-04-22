package cloud.chlora.management.pot.domain.port;

import cloud.chlora.management.pot.domain.model.Pot;
import cloud.chlora.management.device.domain.model.Device;
import cloud.chlora.management.pot.domain.model.PotSummary;

import java.util.List;
import java.util.Optional;

public interface PotReadRepository {

    Optional<Pot> findByPotId(String potId);

    List<PotSummary> findAllExisting(
            String search,
            String sortColumn, String sortDirection,
            int limit, int offset
    );

    long countExisting(String search);

    List<Pot> findAllAsList();

    List<Device> findDevicesByPotId(String potId);

    boolean existsByPotName(String potName);

    boolean existsByPotId(String potId);
}