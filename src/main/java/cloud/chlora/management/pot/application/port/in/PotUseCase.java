package cloud.chlora.management.pot.application.port.in;

import cloud.chlora.management.pot.adapter.in.web.request.PotCreateRequest;
import cloud.chlora.management.pot.adapter.in.web.request.PotUpdateRequest;
import cloud.chlora.management.pot.adapter.in.web.response.*;

public interface PotUseCase {

    PagedPotResponse findAll(
            int page, int size, String search, String sort, String order
    );

    PotGetResponse findByPotId(String potId);

    PotListResponse getPotList();

    PotCreateResponse createPot(PotCreateRequest request);

    PotUpdateResponse updatePot(String potId, PotUpdateRequest request);

    void deletePot(String potId);
}