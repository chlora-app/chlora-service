package cloud.chlora.pipeline.dashboard.adapter.in.web;

import cloud.chlora.pipeline.dashboard.application.service.SensorHistoryQueryService;
import cloud.chlora.pipeline.dashboard.domain.model.SensorHistory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class SensorHistoryController {

    private final SensorHistoryQueryService service;

    @GetMapping(value = "/sensor-history")
    public ResponseEntity<@NonNull SensorHistory> getSensorHistory(
            @RequestParam("potId") String potId,
            @RequestParam("range") String range
    ) {
        SensorHistory sensorHistory = service.execute(potId, range);
        return ResponseEntity.ok(sensorHistory);
    }
}