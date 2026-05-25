package com.symmetricsquad.hms_backend.controller;

import com.symmetricsquad.hms_backend.dto.request.ChangePasswordRequest;
import com.symmetricsquad.hms_backend.dto.response.*;
import com.symmetricsquad.hms_backend.service.AdminService;
import com.symmetricsquad.hms_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    // GET /api/admin/dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard(
            @RequestParam(defaultValue = "5") int recentSize) {
        return ResponseEntity.ok(adminService.getDashboard(recentSize));
    }

    // ── Appointments ──────────────────────────────────────────────────────────

    // GET /api/admin/appointments
    // Admin-only: sees all statuses including NO_SHOW
    @GetMapping("/appointments")
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointments(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return ResponseEntity.ok(adminService.getAllAppointmentsAdmin(pageable));
    }

    // PATCH /api/admin/appointments/{id}/no-show
    @PatchMapping("/appointments/{id}/no-show")
    public ResponseEntity<String> markNoShow(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.markNoShow(id));
    }

    // ── Contact Queries ───────────────────────────────────────────────────────

    // GET /api/admin/contact-queries/unread
    // Kept from original AdminController
    @GetMapping("/contact-queries/unread")
    public ResponseEntity<Page<ContactQueryResponse>> getUnreadContactQueries(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(adminService.getAllContactQueries(false, pageable));
    }

    // GET /api/admin/contact-queries/read
    // Kept from original AdminController
    @GetMapping("/contact-queries/read")
    public ResponseEntity<Page<ContactQueryResponse>> getReadContactQueries(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(adminService.getAllContactQueries(true, pageable));
    }

    // GET /api/admin/contact-queries/{id}
    // Kept from original AdminController
    @GetMapping("/contact-queries/{id}")
    public ResponseEntity<ContactQueryResponse> getContactQueryById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getContactQueryById(id));
    }

    // PATCH /api/admin/contact-queries/{id}/remark
    // Kept from original AdminController (was @PatchMapping)
    @PatchMapping("/contact-queries/{id}/remark")
    public ResponseEntity<ContactQueryResponse> addAdminRemark(
            @PathVariable Long id,
            @RequestBody String remark) {
        return ResponseEntity.ok(adminService.respondToContactQuery(id, remark));
    }

    // DELETE /api/admin/contact-queries/{id}
    @DeleteMapping("/contact-queries/{id}")
    public ResponseEntity<String> deleteContactQuery(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deleteContactQuery(id));
    }

    // ── User & Doctor oversight ───────────────────────────────────────────────

    // GET /api/admin/users?includeInactive=false
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return ResponseEntity.ok(adminService.getAllUsers(includeInactive, pageable));
    }

    // GET /api/admin/doctors?includeInactive=false
    @GetMapping("/doctors")
    public ResponseEntity<Page<DoctorResponse>> getAllDoctors(
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return ResponseEntity.ok(adminService.getAllDoctors(includeInactive, pageable));
    }

    // PUT /api/admin/{id}/change-password
    // Kept from original AdminController — delegates to UserService
    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok("Password changed successfully");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }
}
