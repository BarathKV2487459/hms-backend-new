package com.symmetricsquad.hms_backend.repository;

import com.symmetricsquad.hms_backend.model.DoctorProfile;
import com.symmetricsquad.hms_backend.model.DoctorSpecialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {

    // ── Used by DoctorService ─────────────────────────────────────────────────

    Optional<DoctorProfile> findByUserId(Long userId);

    Page<DoctorProfile> findBySpecialization(DoctorSpecialization specialization, Pageable pageable);

    // ── Used by AdminService — bypasses @Where(is_active=true) ───────────────

    @Query(value = "SELECT * FROM doctor_profiles", nativeQuery = true)
    Page<DoctorProfile> findAllIncludingInactive(Pageable pageable);

    // Top-N recently active doctors
    List<DoctorProfile> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    // Grouped count per specialization name
    @Query("SELECT d.specialization.specializationName, COUNT(d) FROM DoctorProfile d GROUP BY d.specialization.specializationName")
    List<Object[]> countGroupedBySpecialization();
}
