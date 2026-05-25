package com.symmetricsquad.hms_backend.repository;

import com.symmetricsquad.hms_backend.model.DoctorProfile;
import com.symmetricsquad.hms_backend.model.MedicalHistory;
import com.symmetricsquad.hms_backend.model.PatientProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {

    // ── Used by MedicalHistoryService.getRecordsByPatient ────────────────────
    // Default sort is handled by the Pageable passed in (createdAt desc recommended)

    Page<MedicalHistory> findByPatient(PatientProfile patient, Pageable pageable);

    // ── Used by MedicalHistoryService.getRecordsByDoctor ─────────────────────

    Page<MedicalHistory> findByRecordedBy(DoctorProfile recordedBy, Pageable pageable);
}
