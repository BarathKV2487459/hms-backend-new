package com.symmetricsquad.hms_backend.controller;

import com.symmetricsquad.hms_backend.dto.request.AppointmentRequest;
import com.symmetricsquad.hms_backend.dto.response.AppointmentResponse;
import com.symmetricsquad.hms_backend.model.enums.AppointmentStatus;
import com.symmetricsquad.hms_backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // POST /api/appointments
    // Kept from original
    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.bookAppointment(request));
    }

    // GET /api/appointments
    // Kept from original — now paginated + filterable
    // All filter params optional: ?doctorId=1&date=2025-06-01&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointments(
            @RequestParam(required = false) Long patientUserId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(appointmentService.getAppointments(
                patientUserId, doctorId, date, startTime, endTime, status, pageable));
    }

    // GET /api/appointments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    // GET /api/appointments/user/{userId}
    // Kept from original — convenience shorthand, delegates to getAppointments with userId filter
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AppointmentResponse>> getAppointmentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending());
        return ResponseEntity.ok(appointmentService.getAppointments(
                userId, null, null, null, null, null, pageable));
    }

    // GET /api/appointments/doctor/{doctorId}
    // Kept from original — convenience shorthand
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Page<AppointmentResponse>> getAppointmentsByDoctorId(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").ascending());
        return ResponseEntity.ok(appointmentService.getAppointments(
                null, doctorId, null, null, null, null, pageable));
    }

    // PATCH /api/appointments/{id}/cancel-by-user
    // Kept from original
    @PatchMapping("/{id}/cancel-by-user")
    public ResponseEntity<String> cancelByUser(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(appointmentService.cancelByUser(id, userId));
    }

    // PATCH /api/appointments/{id}/cancel-by-doctor
    // Kept from original
    @PatchMapping("/{id}/cancel-by-doctor")
    public ResponseEntity<String> cancelByDoctor(
            @PathVariable Long id,
            @RequestParam Long doctorId) {
        return ResponseEntity.ok(appointmentService.cancelByDoctor(id, doctorId));
    }

    // PATCH /api/appointments/{id}/complete
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<String> confirmAppointment(
            @PathVariable Long id,
            @RequestParam Long doctorId) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(id, doctorId));
    }

    // PATCH /api/appointments/{id}/complete
    @PatchMapping("/{id}/complete")
    public ResponseEntity<String> completeAppointment(
            @PathVariable Long id,
            @RequestParam Long doctorId) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id, doctorId));
    }

    // GET /api/appointments/slot-available
    // Pre-validation before submitting booking form
    @GetMapping("/slot-available")
    public ResponseEntity<Boolean> isSlotAvailable(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return ResponseEntity.ok(
                appointmentService.isSlotAvailable(doctorId, date, startTime, endTime));
    }
}
