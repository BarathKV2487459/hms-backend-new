package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.dto.request.AppointmentRequest;
import com.symmetricsquad.hms_backend.dto.response.AppointmentResponse;
import com.symmetricsquad.hms_backend.model.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AppointmentService {

    /**
     * Book an appointment.
     * - Validates slot availability (no overlap for same doctor + date).
     * - Sets status = PENDING.
     * - Auto-creates PatientProfile if this is the user's first booking.
     */
    AppointmentResponse bookAppointment(AppointmentRequest request);

    /**
     * Get all appointments — paginated + optional filters.
     *
     * All filter params are optional (nullable). Any combination is valid:
     * @param patientUserId  filter by the booking user's ID
     * @param doctorId       filter by DoctorProfile ID
     * @param date           filter by exact appointment date
     * @param startTime      filter by start time (inclusive)
     * @param endTime        filter by end time (inclusive)
     * @param status         filter by appointment status
     *
     * Admin callers: no status exclusion applied.
     * Patient/Doctor callers: service excludes NO_SHOW automatically.
     */
    Page<AppointmentResponse> getAppointments(
            Long patientUserId,
            Long doctorId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            AppointmentStatus status,
            Pageable pageable
    );

    /** Get a single appointment by ID */
    AppointmentResponse getAppointmentById(Long id);

    /**
     * Cancel by the patient (user).
     * Sets status = CANCELLED_BY_USER.
     * Only allowed if current status = PENDING or CONFIRMED.
     */
    String cancelByUser(Long appointmentId, Long userId);

    /**
     * Cancel by the doctor.
     * Sets status = CANCELLED_BY_DOCTOR.
     * Only allowed if current status = PENDING or CONFIRMED.
     */
    String cancelByDoctor(Long appointmentId, Long doctorId);

    /**
     * Mark appointment as CONFIRMED (DOCTOR ONLY).
     * Only allowed if current status = PENDING.
     */
    String confirmAppointment(Long appointmentId, Long doctorId);

    /**
     * Mark appointment as COMPLETED (doctor only).
     * Only allowed if current status = CONFIRMED.
     */
    String completeAppointment(Long appointmentId, Long doctorId);

    /**
     * Check if a doctor's slot is already taken.
     * Used before booking to validate no overlap.
     * Exposed here so controllers can pre-validate before submitting.
     */
    boolean isSlotAvailable(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime);
}