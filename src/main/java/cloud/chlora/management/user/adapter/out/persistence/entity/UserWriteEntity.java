package cloud.chlora.management.user.adapter.out.persistence.entity;

import cloud.chlora.management.user.domain.model.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity(name = "UserWriteEntity")
public class UserWriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", updatable = false)
    private String userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    void prePersist() {
        if (userId == null) {
            userId = "U-" + RandomStringUtils.secure().nextAlphanumeric(6).toLowerCase();
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