package com.symmetricsquad.hms_backend.dto.response;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AdminDashboardResponse {

    // ── Counts ────────────────────────────────────────────────────────────────

    private Long totalPatients;
    private Long totalDoctors;
    private Long totalAppointments;
    private Long totalMedicalHistoryRecords;
    private Long openContactQueries;        // isRead = false

    /** Doctors grouped by specialization name → count
     *  e.g. { "Cardiology": 4, "Neurology": 2 }  */
    private Map<String, Long> doctorsBySpecialization;

    /** Appointments grouped by status → count
     *  e.g. { "PENDING": 10, "CONFIRMED": 5, "COMPLETED": 80 } */
    private Map<String, Long> appointmentsByStatus;

    // ── Top-N lists (size controlled by caller via recentSize param) ──────────

    private List<AppointmentResponse> upcomingAppointments;   // CONFIRMED, date >= today
    private List<PatientResponse>     recentActivePatients;   // latest by updatedAt
    private List<DoctorResponse>      recentActiveDoctors;    // latest by updatedAt
}