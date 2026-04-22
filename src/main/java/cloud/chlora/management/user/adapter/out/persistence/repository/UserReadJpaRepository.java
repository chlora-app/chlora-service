package cloud.chlora.management.user.adapter.out.persistence.repository;

import cloud.chlora.management.user.adapter.out.persistence.entity.UserReadEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserReadJpaRepository extends JpaRepository<UserReadEntity, Long> {

    Optional<UserReadEntity> findByUserId(String userId);

    @Query(value = """
            SELECT u.user_id FROM users u
            WHERE u.deleted_at IS NULL
            """, nativeQuery = true)
    List<String> findAllActiveUserIds();

    @Query(value = """
            SELECT * FROM users u
            WHERE u.deleted_at IS NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(u.name)    LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.email)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.user_id) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (CAST(:role AS text) IS NULL OR u.role = :role)
            """,
            countQuery = """
            SELECT COUNT(*) FROM users u
            WHERE u.deleted_at IS NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(u.name)    LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.email)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.user_id) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (CAST(:role AS text) IS NULL OR u.role = :role)
            """,
            nativeQuery = true)
    Page<@NonNull UserReadEntity> findAllActive(
            @Param("search") String search,
            @Param("role") String role,
            Pageable pageable
    );

    @Query(value = """
            SELECT * FROM users u
            WHERE u.deleted_at IS NOT NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(u.name)    LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.email)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.user_id) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (CAST(:role AS text) IS NULL OR u.role = :role)
            """,
            countQuery = """
            SELECT COUNT(*) FROM users u
            WHERE u.deleted_at IS NOT NULL
              AND (CAST(:search AS text) IS NULL
                   OR LOWER(u.name)    LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.email)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.user_id) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (CAST(:role AS text) IS NULL OR u.role = :role)
            """,
            nativeQuery = true)
    Page<@NonNull UserReadEntity> findAllDeleted(
            @Param("search") String search,
            @Param("role") String role,
            Pageable pageable
    );
}