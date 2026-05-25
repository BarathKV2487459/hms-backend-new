package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.dto.request.ChangePasswordRequest;
import com.symmetricsquad.hms_backend.dto.request.DoctorRequest;
import com.symmetricsquad.hms_backend.dto.response.AppointmentResponse;
import com.symmetricsquad.hms_backend.dto.response.DoctorResponse;
import com.symmetricsquad.hms_backend.dto.response.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {

    /** Add a new doctor (creates User with role=DOCTOR + DoctorProfile) */
    DoctorResponse addDoctor(DoctorRequest request);

    /** Get all active doctors — paginated */
    Page<DoctorResponse> getAllDoctors(Pageable pageable);

    /** Get a single doctor by their DoctorProfile ID */
    DoctorResponse getDoctorById(Long id);

    /** Update DoctorProfile fields (fees, bio, specialization, etc.) */
    DoctorResponse updateDoctor(Long id, DoctorRequest request);

    /** Soft-delete doctor (User + DoctorProfile + future appointments) */
    String deleteDoctor(Long id);

    /** Change doctor's password with old-password verification */
    void changePassword(Long doctorId, ChangePasswordRequest request);

    /**
     * Get upcoming confirmed appointments for a doctor.
     * "Upcoming" = appointmentDate >= today AND status = CONFIRMED.
     * Paginated so the doctor's dashboard can load incrementally.
     */
    Page<AppointmentResponse> getUpcomingAppointments(Long doctorId, Pageable pageable);

    /**
     * Get all patients assigned to a doctor — paginated.
     * Useful for the doctor's patient panel.
     */
    Page<PatientResponse> getPatientsByDoctor(Long doctorId, Pageable pageable);
}
