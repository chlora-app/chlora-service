package cloud.chlora.management.cluster.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clusters")
@Entity(name = "ClusterWriteEntity")
public class ClusterWriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cluster_id", updatable = false)
    private String clusterId;

    @Column(name = "cluster_name", nullable = false)
    private String clusterName;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    void prePersist() {
        if (clusterId == null) {
            clusterId = "CL-" + RandomStringUtils.secure().nextAlphanumeric(6).toLowerCase();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}