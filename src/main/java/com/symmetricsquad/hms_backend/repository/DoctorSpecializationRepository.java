package com.symmetricsquad.hms_backend.repository;

import com.symmetricsquad.hms_backend.model.DoctorSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorSpecializationRepository extends JpaRepository<DoctorSpecialization, Long> {

    // ── Used by DoctorSpecializationService — duplicate name guard ────────────

    boolean existsBySpecializationNameIgnoreCase(String specializationName);
}
