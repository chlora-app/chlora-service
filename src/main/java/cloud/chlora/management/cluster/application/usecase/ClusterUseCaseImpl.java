package cloud.chlora.management.cluster.application.usecase;

import cloud.chlora.management.cluster.adapter.in.web.request.ClusterCreateRequest;
import cloud.chlora.management.cluster.adapter.in.web.request.ClusterUpdateRequest;
import cloud.chlora.management.cluster.adapter.in.web.response.*;
import cloud.chlora.management.cluster.application.port.in.ClusterUseCase;
import cloud.chlora.management.cluster.domain.model.Cluster;
import cloud.chlora.management.cluster.domain.port.ClusterReadRepository;
import cloud.chlora.management.cluster.domain.port.ClusterWriteRepository;
import cloud.chlora.management.shared.error.IotErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import cloud.chlora.management.device.adapter.in.web.response.DeviceListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterUseCaseImpl implements ClusterUseCase {

    private final ClusterReadRepository  readRepository;
    private final ClusterWriteRepository writeRepository;

    // ── Queries ───────────────────────────────────────────────────────────────

    @Override
    public PagedClusterResponse findAll(
            int page, int size, String search, String sort, String order
    ) {
        if (page < 1) throw AppException.of(IotErrorCode.PAGE_LOWER_THAN_ONE);
        if (size < 1) throw AppException.of(IotErrorCode.SIZE_LOWER_THAN_ONE);

        int offset = (page - 1) * size;

        List<ClusterSummaryResponse> clusters = readRepository
                .findAllExisting(search, resolveColumn(sort), resolveDir(order), size, offset)
                .stream()
                .map(c -> new ClusterSummaryResponse(
                        c.clusterId(),
                        c.clusterName(),
                        readRepository.countDevices(c.clusterId())
                ))
                .toList();

        long total      = readRepository.countExisting(search);
        int  totalPages = (int) Math.ceil((double) total / size);

        return new PagedClusterResponse(total, page, size, totalPages, clusters);
    }

    @Override
    public ClusterGetResponse findByClusterId(String clusterId) {
        Cluster cluster = requireCluster(clusterId);
        if (cluster.isDeleted()) throw AppException.of(IotErrorCode.CLUSTER_ALREADY_DELETED);

        long deviceCount = readRepository.countDevices(clusterId);
        List<DeviceListResponse> devices = readRepository.findDevicesByClusterId(clusterId)
                .stream()
                .map(d -> new DeviceListResponse(d.deviceId(), d.deviceName(), d.deviceType(), d.status()))
                .toList();

        return new ClusterGetResponse(cluster.clusterId(), cluster.clusterName(), deviceCount, devices);
    }

    @Override
    public ClusterListResponse getClusterList() {
        List<ClusterListResponse.ClusterInfo> list = readRepository.findAllAsList()
                .stream()
                .map(c -> new ClusterListResponse.ClusterInfo(c.clusterName(), c.clusterId()))
                .toList();
        return new ClusterListResponse(list);
    }

    // ── Commands ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ClusterCreateResponse createCluster(ClusterCreateRequest request) {
        if (readRepository.existsByClusterName(request.clusterName())) {
            log.warn("[ClusterUseCase] createCluster - name exists: {}", request.clusterName());
            throw AppException.of(IotErrorCode.CLUSTER_NAME_ALREADY_EXISTS);
        }

        Cluster saved = writeRepository.create(request);
        log.info("[ClusterUseCase] created clusterId={}", saved.clusterId());
        return new ClusterCreateResponse(saved.clusterId(), saved.clusterName(), saved.createdAt());
    }

    @Override
    @Transactional
    public ClusterUpdateResponse updateCluster(String clusterId, ClusterUpdateRequest request) {
        if (request.clusterName() == null || request.clusterName().isBlank()) {
            throw AppException.of(IotErrorCode.CLUSTER_UPDATE_EMPTY);
        }

        Cluster existing = requireCluster(clusterId);

        if (existing.clusterName().equals(request.clusterName())) {
            return new ClusterUpdateResponse(existing.clusterId(), existing.clusterName(), existing.updatedAt());
        }

        if (readRepository.existsByClusterName(request.clusterName())) {
            log.warn("[ClusterUseCase] updateCluster - name exists: {}", request.clusterName());
            throw AppException.of(IotErrorCode.CLUSTER_NAME_ALREADY_EXISTS);
        }

        Cluster updated = writeRepository.update(clusterId, request);
        log.info("[ClusterUseCase] updated clusterId={}", clusterId);
        return new ClusterUpdateResponse(updated.clusterId(), updated.clusterName(), updated.updatedAt());
    }

    @Override
    @Transactional
    public void deleteCluster(String clusterId) {
        Cluster cluster = requireCluster(clusterId);
        if (cluster.isDeleted()) throw AppException.of(IotErrorCode.CLUSTER_ALREADY_DELETED);

        writeRepository.softDelete(clusterId);
        writeRepository.softDeleteDevicesByClusterId(clusterId);
        log.info("[ClusterUseCase] deleted clusterId={}", clusterId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Cluster requireCluster(String clusterId) {
        return readRepository.findByClusterId(clusterId)
                .orElseThrow(() -> AppException.of(IotErrorCode.CLUSTER_NOT_FOUND));
    }

    private String resolveColumn(String sort) {
        return switch (sort == null ? "" : sort) {
            case "clusterId", "cluster_id"     -> "clusterId";
            case "clusterName", "cluster_name" -> "clusterName";
            default                            -> "createdAt";
        };
    }

    private String resolveDir(String order) {
        return "desc".equalsIgnoreCase(order) ? "DESC" : "ASC";
    }
}