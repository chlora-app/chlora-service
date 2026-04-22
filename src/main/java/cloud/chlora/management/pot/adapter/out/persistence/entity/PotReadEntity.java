package cloud.chlora.management.pot.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@Getter
@Immutable
@NoArgsConstructor
@Table(name = "pots")
@Entity(name = "PotReadEntity")
public class PotReadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pot_id", insertable = false, updatable = false)
    private String potId;

    @Column(name = "pot_name")
    private String potName;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}