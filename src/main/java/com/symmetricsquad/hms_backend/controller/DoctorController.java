package com.symmetricsquad.hms_backend.controller;

import com.symmetricsquad.hms_backend.dto.request.ChangePasswordRequest;
import com.symmetricsquad.hms_backend.dto.request.DoctorRequest;
import com.symmetricsquad.hms_backend.dto.response.AppointmentResponse;
import com.symmetricsquad.hms_backend.dto.response.DoctorResponse;
import com.symmetricsquad.hms_backend.dto.response.PatientResponse;
import com.symmetricsquad.hms_backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // POST /api/doctors
    // Kept from original
    @PostMapping
    public ResponseEntity<DoctorResponse> addDoctor(
            @RequestBody DoctorRequest request) {
        return ResponseEntity.ok(doctorService.addDoctor(request));
    }

    // GET /api/doctors
    // Kept from original — now paginated
    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> getAllDoctors(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(doctorService.getAllDoctors(pageable));
    }

    // GET /api/doctors/{id}
    // Kept from original
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    // PUT /api/doctors/{id}
    // Kept from original
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long id,
            @RequestBody DoctorRequest request) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, request));
    }

    // DELETE /api/doctors/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.deleteDoctor(id));
    }

    // PUT /api/doctors/{id}/change-password
    // Kept from original
    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        doctorService.changePassword(id, request);
        return ResponseEntity.ok("Password changed successfully");
    }

    // GET /api/doctors/{id}/upcoming-appointments
    // New — doctor dashboard feed
    @GetMapping("/{id}/upcoming-appointments")
    public ResponseEntity<Page<AppointmentResponse>> getUpcomingAppointments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").ascending());
        return ResponseEntity.ok(doctorService.getUpcomingAppointments(id, pageable));
    }

    // GET /api/doctors/{id}/patients
    // Moved from PatientController /patients/doctor/{doctorId} — semantically belongs here
    @GetMapping("/{id}/patients")
    public ResponseEntity<Page<PatientResponse>> getPatientsByDoctor(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(doctorService.getPatientsByDoctor(id, pageable));
    }
}
