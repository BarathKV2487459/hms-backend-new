package com.symmetricsquad.hms_backend.repository;

import com.symmetricsquad.hms_backend.model.User;
import com.symmetricsquad.hms_backend.model.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ── Used by login and registration ────────────────────────────────────────

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // ── Used by AdminService — bypasses @Where(is_active=true) ───────────────
    // nativeQuery = false so Hibernate still maps to the entity,
    // but the JPQL targets the table directly without the @Where filter.

    @Query("SELECT u FROM User u WHERE u.role = :role")
    Page<User> findByRole(UserRole role, Pageable pageable);

    /**
     * Returns ALL users regardless of isActive — used by admin oversight.
     * The @Where clause on the entity is bypassed because we select directly.
     */
    @Query(value = "SELECT * FROM users", nativeQuery = true)
    Page<User> findAllIncludingInactive(Pageable pageable);

    // Count by role — used for totalPatients / totalDoctors
    long countByRole(UserRole role);
}
