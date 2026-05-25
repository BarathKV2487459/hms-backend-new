package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.dto.request.MedicalHistoryRequest;
import com.symmetricsquad.hms_backend.dto.response.MedicalHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicalHistoryService {

    /**
     * Create a new medical history record for a patient.
     * - doctorId is taken from the authenticated doctor's session.
     * - Not tied to any appointment — can be created independently.
     */
    MedicalHistoryResponse createRecord(Long patientId, Long doctorId, MedicalHistoryRequest request);

    /** Get a single medical history record by ID */
    MedicalHistoryResponse getRecordById(Long id);

    /**
     * Get all medical history records for a patient — paginated.
     * Sorted by createdAt desc by default so most recent shows first.
     */
    Page<MedicalHistoryResponse> getRecordsByPatient(Long patientId, Pageable pageable);

    /**
     * Get all records created by a specific doctor — paginated.
     * Useful for a doctor reviewing their own entries.
     */
    Page<MedicalHistoryResponse> getRecordsByDoctor(Long doctorId, Pageable pageable);

    /**
     * Update an existing record (doctor can amend vitals or clinical data).
     * Only the doctor who created the record can update it.
     */
    MedicalHistoryResponse updateRecord(Long id, Long doctorId, MedicalHistoryRequest request);

    /** Soft-delete a medical history record */
    String deleteRecord(Long id);
}
