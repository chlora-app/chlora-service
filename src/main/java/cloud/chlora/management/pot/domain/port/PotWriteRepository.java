package cloud.chlora.management.pot.domain.port;

import cloud.chlora.management.pot.adapter.in.web.request.PotCreateRequest;
import cloud.chlora.management.pot.adapter.in.web.request.PotUpdateRequest;
import cloud.chlora.management.pot.domain.model.Pot;

public interface PotWriteRepository {

    Pot create(PotCreateRequest request);

    Pot update(String potId, PotUpdateRequest request);

    void softDelete(String potId);

    void softDeleteDevicesByPotId(String potId);
}