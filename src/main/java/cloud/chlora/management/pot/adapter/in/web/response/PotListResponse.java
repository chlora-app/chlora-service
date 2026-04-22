package cloud.chlora.management.pot.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PotListResponse(List<PotInfo> list) {

    public record PotInfo(
            @JsonProperty("label") String potName,
            @JsonProperty("value") String potId
    ) {}
}