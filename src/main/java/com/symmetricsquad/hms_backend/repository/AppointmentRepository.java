package com.symmetricsquad.hms_backend.repository;


import com.symmetricsquad.hms_backend.model.Appointment;
import com.symmetricsquad.hms_backend.model.DoctorProfile;
import com.symmetricsquad.hms_backend.model.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Repository
public interface AppointmentRepository
        extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    // ── Used by AppointmentService.getAppointments (filtered queries) ─────────
    // The dynamic Specification approach covers all filter combos — no extra
    // derived methods needed for that. JpaSpecificationExecutor provides
    // findAll(Specification, Pageable) out of the box.

    // ── Used by DoctorService.getUpcomingAppointments ─────────────────────────

    Page<Appointment> findByDoctorAndAppointmentDateGreaterThanEqualAndStatus(
            DoctorProfile doctor,
            LocalDate date,
            AppointmentStatus status,
            Pageable pageable
    );

    // ── Used by DoctorService.deleteDoctor — fetch future non-terminal bookings ─

    List<Appointment> findByDoctorAndAppointmentDateGreaterThanEqualAndStatusNotIn(
            DoctorProfile doctor,
            LocalDate date,
            Set<AppointmentStatus> excludedStatuses
    );

    // ── Used by AppointmentService.isSlotAvailable ────────────────────────────
    /**
     * Returns true if the doctor has any active (non-cancelled, non-no-show)
     * appointment that overlaps with the requested [startTime, endTime] window
     * on the given date.
     *
     * Overlap condition: existing.startTime < :endTime AND existing.endTime > :startTime
     * This catches all cases — partial overlap, full containment, exact match.
     *
     * :excludedStatuses should be the terminal statuses that free the slot:
     *   CANCELLED_BY_USER, CANCELLED_BY_DOCTOR, NO_SHOW
     */
    @Query("""
        SELECT COUNT(a) > 0 FROM Appointment a
        WHERE a.doctor.id      = :doctorId
          AND a.appointmentDate = :date
          AND a.isActive        = true
          AND a.status         NOT IN :excludedStatuses
          AND a.startTime       < :endTime
          AND a.endTime         > :startTime
    """)
    boolean existsOverlappingAppointment(
            @Param("doctorId")         Long doctorId,
            @Param("date")             LocalDate date,
            @Param("startTime")        LocalTime startTime,
            @Param("endTime")          LocalTime endTime,
            @Param("excludedStatuses") Set<AppointmentStatus> excludedStatuses
    );

    // ── Used by AdminService.getAllAppointmentsAdmin ───────────────────────────
    // Standard findAll(Pageable) from JpaRepository is used directly there —
    // no custom method needed. The @Where filter on the entity still applies,
    // but NO_SHOW records are active (isActive=true), so they show up normally.

    // Count by status — used for appointmentsByStatus breakdown
    long countByStatus(AppointmentStatus status);

    // Top-N upcoming CONFIRMED appointments from today
    List<Appointment> findByStatusAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscStartTimeAsc(
            AppointmentStatus status, LocalDate date, Pageable pageable);

    // Grouped count per status — used to build the map in one query
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countGroupedByStatus();
}
