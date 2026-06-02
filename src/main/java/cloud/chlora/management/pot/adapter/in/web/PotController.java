package cloud.chlora.management.pot.adapter.in.web;

import cloud.chlora.management.pot.adapter.in.web.request.PotCreateRequest;
import cloud.chlora.management.pot.adapter.in.web.request.PotUpdateRequest;
import cloud.chlora.management.pot.adapter.in.web.response.*;
import cloud.chlora.management.pot.application.port.in.PotUseCase;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pots")
@RequiredArgsConstructor
public class PotController {

    private final PotUseCase potUseCase;

    @GetMapping
    public ResponseEntity<@NonNull PagedPotResponse> findAll(
            @RequestParam(defaultValue = "1")          int page,
            @RequestParam(defaultValue = "10")         int size,
            @RequestParam(required = false)            String search,
            @RequestParam(defaultValue = "created_at") String sort,
            @RequestParam(defaultValue = "asc")        String order
    ) {
        return ResponseEntity.ok(potUseCase.findAll(page, size, search, sort, order));
    }

    @GetMapping("/list")
    public ResponseEntity<@NonNull PotListResponse> getList() {
        return ResponseEntity.ok(potUseCase.getPotList());
    }

    @GetMapping("/{potId}")
    public ResponseEntity<@NonNull PotGetResponse> findOne(@PathVariable String potId) {
        return ResponseEntity.ok(potUseCase.findByPotId(potId));
    }

    @PostMapping
    public ResponseEntity<@NonNull PotCreateResponse> create(@Valid @RequestBody PotCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(potUseCase.createPot(request));
    }

    @PatchMapping("/{potId}")
    public ResponseEntity<@NonNull PotUpdateResponse> update(
            @PathVariable String potId,
            @Valid @RequestBody PotUpdateRequest request) {
        return ResponseEntity.ok(potUseCase.updatePot(potId, request));
    }

    @DeleteMapping("/{potId}")
    public ResponseEntity<@NonNull Void> delete(@PathVariable String potId) {
        potUseCase.deletePot(potId);
        return ResponseEntity.noContent().build();
    }
}