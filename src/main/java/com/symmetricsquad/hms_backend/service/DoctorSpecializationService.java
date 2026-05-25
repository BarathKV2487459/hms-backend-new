package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.dto.request.DoctorSpecializationRequest;
import com.symmetricsquad.hms_backend.dto.response.DoctorSpecializationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorSpecializationService {

    /** Add a new specialization */
    DoctorSpecializationResponse addSpecialization(DoctorSpecializationRequest request);

    /** Get all active specializations — paginated */
    Page<DoctorSpecializationResponse> getAllSpecializations(Pageable pageable);

    /** Get a single specialization by ID */
    DoctorSpecializationResponse getSpecializationById(Long id);

    /** Update specialization name/description */
    DoctorSpecializationResponse updateSpecialization(Long id, DoctorSpecializationRequest request);

    /** Soft-delete a specialization (doctors remain, reassign separately) */
    String deleteSpecialization(Long id);
}