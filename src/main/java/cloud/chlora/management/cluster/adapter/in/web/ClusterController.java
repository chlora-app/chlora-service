package cloud.chlora.management.cluster.adapter.in.web;

import cloud.chlora.management.cluster.adapter.in.web.request.ClusterCreateRequest;
import cloud.chlora.management.cluster.adapter.in.web.request.ClusterUpdateRequest;
import cloud.chlora.management.cluster.adapter.in.web.response.*;
import cloud.chlora.management.cluster.application.port.in.ClusterUseCase;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clusters")
@RequiredArgsConstructor
public class ClusterController {

    private final ClusterUseCase clusterUseCase;

    @GetMapping
    public ResponseEntity<@NonNull PagedClusterResponse> findAll(
            @RequestParam(defaultValue = "1")          int page,
            @RequestParam(defaultValue = "10")         int size,
            @RequestParam(required = false)            String search,
            @RequestParam(defaultValue = "created_at") String sort,
            @RequestParam(defaultValue = "asc")        String order
    ) {
        return ResponseEntity.ok(clusterUseCase.findAll(page, size, search, sort, order));
    }

    @GetMapping("/list")
    public ResponseEntity<@NonNull ClusterListResponse> getList() {
        return ResponseEntity.ok(clusterUseCase.getClusterList());
    }

    @GetMapping("/{clusterId}")
    public ResponseEntity<@NonNull ClusterGetResponse> findOne(@PathVariable String clusterId) {
        return ResponseEntity.ok(clusterUseCase.findByClusterId(clusterId));
    }

    @PostMapping
    public ResponseEntity<@NonNull ClusterCreateResponse> create(
            @Valid @RequestBody ClusterCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clusterUseCase.createCluster(request));
    }

    @PatchMapping("/{clusterId}")
    public ResponseEntity<@NonNull ClusterUpdateResponse> update(
            @PathVariable String clusterId,
            @Valid @RequestBody ClusterUpdateRequest request) {
        return ResponseEntity.ok(clusterUseCase.updateCluster(clusterId, request));
    }

    @DeleteMapping("/{clusterId}")
    public ResponseEntity<@NonNull Void> delete(@PathVariable String clusterId) {
        clusterUseCase.deleteCluster(clusterId);
        return ResponseEntity.noContent().build();
    }
}