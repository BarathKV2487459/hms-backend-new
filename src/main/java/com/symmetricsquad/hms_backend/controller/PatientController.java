package com.symmetricsquad.hms_backend.controller;

import com.symmetricsquad.hms_backend.dto.request.MedicalHistoryRequest;
import com.symmetricsquad.hms_backend.dto.request.PatientProfileRequest;
import com.symmetricsquad.hms_backend.dto.response.MedicalHistoryResponse;
import com.symmetricsquad.hms_backend.dto.response.PatientResponse;
import com.symmetricsquad.hms_backend.service.MedicalHistoryService;
import com.symmetricsquad.hms_backend.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final MedicalHistoryService medicalHistoryService;

    // POST /api/patients
    // Kept from original — manually create a patient profile for a user
    @PostMapping
    public ResponseEntity<PatientResponse> createPatientProfile(
            @RequestParam Long userId,
            @RequestBody PatientProfileRequest request) {
        return ResponseEntity.ok(patientService.createPatientProfile(userId, request));
    }

    // GET /api/patients
    // Kept from original — now paginated
    @GetMapping
    public ResponseEntity<Page<PatientResponse>> getAllPatients(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    // GET /api/patients/{id}
    // Kept from original
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    // GET /api/patients/by-user/{userId}
    // New — look up patient profile by the user account id
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<PatientResponse> getPatientByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(patientService.getPatientByUserId(userId));
    }

    // PUT /api/patients/{id}
    // Kept from original — now uses PatientProfileRequest DTO instead of raw entity
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @RequestBody PatientProfileRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    // DELETE /api/patients/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.deletePatient(id));
    }

    // GET /api/patients/doctor/{doctorId}
    // Kept from original — now delegates to DoctorService via PatientService
    // (also available at GET /api/doctors/{id}/patients)
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Page<PatientResponse>> getPatientsByDoctorId(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // PatientService delegates the doctor-filter query via the repository
        return ResponseEntity.ok(
                patientService.getPatientsByDoctor(doctorId, pageable));
    }

    // ── Medical History ───────────────────────────────────────────────────────

    // POST /api/patients/{patientId}/medical-history
    // Kept from original — now also requires doctorId param
    @PostMapping("/{patientId}/medical-history")
    public ResponseEntity<MedicalHistoryResponse> createMedicalHistory(
            @PathVariable Long patientId,
            @RequestParam Long doctorId,
            @RequestBody MedicalHistoryRequest request) {
        return ResponseEntity.ok(
                medicalHistoryService.createRecord(patientId, doctorId, request));
    }

    // GET /api/patients/{patientId}/medical-history
    // Kept from original — now paginated, sorted by createdAt desc
    @GetMapping("/{patientId}/medical-history")
    public ResponseEntity<Page<MedicalHistoryResponse>> getMedicalHistory(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(
                medicalHistoryService.getRecordsByPatient(patientId, pageable));
    }

    // GET /api/patients/medical-history/{id}
    // Get a single medical history record by its own ID
    @GetMapping("/medical-history/{id}")
    public ResponseEntity<MedicalHistoryResponse> getMedicalHistoryById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalHistoryService.getRecordById(id));
    }

    // PUT /api/patients/medical-history/{id}
    // Doctor amends a record they created
    @PutMapping("/medical-history/{id}")
    public ResponseEntity<MedicalHistoryResponse> updateMedicalHistory(
            @PathVariable Long id,
            @RequestParam Long doctorId,
            @RequestBody MedicalHistoryRequest request) {
        return ResponseEntity.ok(medicalHistoryService.updateRecord(id, doctorId, request));
    }

    // DELETE /api/patients/medical-history/{id}
    @DeleteMapping("/medical-history/{id}")
    public ResponseEntity<String> deleteMedicalHistory(@PathVariable Long id) {
        return ResponseEntity.ok(medicalHistoryService.deleteRecord(id));
    }
}
