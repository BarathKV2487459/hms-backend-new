package com.symmetricsquad.hms_backend.repository;

import com.symmetricsquad.hms_backend.model.DoctorProfile;
import com.symmetricsquad.hms_backend.model.PatientProfile;
import com.symmetricsquad.hms_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {

    // ── Used by PatientService & AppointmentService ───────────────────────────

    /**
     * Lookup by the owning User — used to check if a profile already
     * exists before auto-creating one on first appointment booking.
     */
    Optional<PatientProfile> findByUser(User user);

    // ── Used by DoctorService.getPatientsByDoctor ─────────────────────────────

    Page<PatientProfile> findByAssignedDoctor(DoctorProfile assignedDoctor, Pageable pageable);

    // Top-N recently active patients
    List<PatientProfile> findAllByOrderByUpdatedAtDesc(Pageable pageable);
}
