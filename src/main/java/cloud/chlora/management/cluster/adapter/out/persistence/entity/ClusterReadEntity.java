package cloud.chlora.management.cluster.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@Getter
@Immutable
@NoArgsConstructor
@Table(name = "clusters")
@Entity(name = "ClusterReadEntity")
public class ClusterReadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cluster_id", insertable = false, updatable = false)
    private String clusterId;

    @Column(name = "cluster_name")
    private String clusterName;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}