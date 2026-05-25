package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.dto.request.PatientProfileRequest;
import com.symmetricsquad.hms_backend.dto.response.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {

    /**
     * Manually create a PatientProfile for an existing user.
     * (Auto-creation on first booking is handled inside AppointmentService.)
     */
    PatientResponse createPatientProfile(Long userId, PatientProfileRequest request);

    /** Get a patient profile by PatientProfile ID */
    PatientResponse getPatientById(Long id);

    /** Get patient profile by User ID */
    PatientResponse getPatientByUserId(Long userId);

    /** Update patient medical profile (age, allergies, conditions, medications) */
    PatientResponse updatePatient(Long id, PatientProfileRequest request);

    /** Get all patients — paginated */
    Page<PatientResponse> getAllPatients(Pageable pageable);

    /** Soft-delete patient profile (called from UserService.deleteUser internally) */
    String deletePatient(Long id);

    Page<PatientResponse> getPatientsByDoctor(Long doctorId, Pageable pageable);
}