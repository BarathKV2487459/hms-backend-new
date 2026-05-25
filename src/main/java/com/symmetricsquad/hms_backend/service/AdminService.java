package com.symmetricsquad.hms_backend.service;


import com.symmetricsquad.hms_backend.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {

    /**
     * Mark an appointment as NO_SHOW.
     * Only admins can set this status.
     * NO_SHOW appointments are hidden from patient and doctor queries.
     */
    String markNoShow(Long appointmentId);

    /**
     * Get ALL appointments including NO_SHOW ones — paginated.
     * Admin-only view; delegates to AppointmentService with no status exclusion.
     */
    Page<AppointmentResponse> getAllAppointmentsAdmin(Pageable pageable);

    // ── Contact Query Management ─────────────────────────────────────────────

    /** Get all contact queries — paginated. Filter by isRead optionally. */
    Page<ContactQueryResponse> getAllContactQueries(Boolean isRead, Pageable pageable);

    /** Get a single contact query by ID */
    ContactQueryResponse getContactQueryById(Long id);

    /** Add/update admin remark and mark query as read */
    ContactQueryResponse respondToContactQuery(Long id, String remark);

    /** Soft-delete a contact query */
    String deleteContactQuery(Long id);

    // ── User & Doctor oversight ──────────────────────────────────────────────

    /**
     * Get all users (patients) — paginated.
     * Admin sees soft-deleted users too if needed (pass includeInactive flag).
     */
    Page<UserResponse> getAllUsers(boolean includeInactive, Pageable pageable);

    /**
     * Get all doctors — paginated.
     * Admin sees soft-deleted doctors too if needed.
     */
    Page<DoctorResponse> getAllDoctors(boolean includeInactive, Pageable pageable);

    /**
     * Builds the full admin dashboard payload.
     * @param recentSize how many items to include in each top-N list (typically 5–10)
     */
    AdminDashboardResponse getDashboard(int recentSize);
}
