package cloud.chlora.management.device.adapter.in.web;

import cloud.chlora.management.device.adapter.in.web.request.DeviceCreateRequest;
import cloud.chlora.management.device.adapter.in.web.request.DeviceUpdateRequest;
import cloud.chlora.management.device.adapter.in.web.response.*;
import cloud.chlora.management.device.application.port.in.DeviceUseCase;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceUseCase deviceUseCase;

    @GetMapping
    public ResponseEntity<@NonNull PagedDeviceResponse> findAll(
            @RequestParam(defaultValue = "1")          int page,
            @RequestParam(defaultValue = "10")         int size,
            @RequestParam(required = false)            String search,
            @RequestParam(defaultValue = "created_at") String sort,
            @RequestParam(defaultValue = "asc")        String order,
            @RequestParam(required = false)            String potId,
            @RequestParam(required = false)            String status
    ) {
        return ResponseEntity.ok(deviceUseCase.findAll(page, size, search, sort, order, potId, status));
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<@NonNull DeviceGetResponse> findOne(@PathVariable String deviceId) {
        return ResponseEntity.ok(deviceUseCase.findByDeviceId(deviceId));
    }

    @PostMapping
    public ResponseEntity<@NonNull DeviceCreateResponse> create(
            @Valid @RequestBody DeviceCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceUseCase.createDevice(request));
    }

    @PatchMapping("/{deviceId}")
    public ResponseEntity<@NonNull DeviceUpdateResponse> update(
            @PathVariable String deviceId,
            @Valid @RequestBody DeviceUpdateRequest request) {
        return ResponseEntity.ok(deviceUseCase.updateDevice(deviceId, request));
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<@NonNull Void> delete(@PathVariable String deviceId) {
        deviceUseCase.deleteDevice(deviceId);
        return ResponseEntity.noContent().build();
    }
}