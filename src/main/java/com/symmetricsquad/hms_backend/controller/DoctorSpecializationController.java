package com.symmetricsquad.hms_backend.controller;

import com.symmetricsquad.hms_backend.dto.request.DoctorSpecializationRequest;
import com.symmetricsquad.hms_backend.dto.response.DoctorSpecializationResponse;
import com.symmetricsquad.hms_backend.service.DoctorSpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor-specializations")
@CrossOrigin
@RequiredArgsConstructor
public class DoctorSpecializationController {

    private final DoctorSpecializationService doctorSpecializationService;

    // GET /api/doctor-specializations
    // Kept from original — now paginated
    @GetMapping
    public ResponseEntity<Page<DoctorSpecializationResponse>> getAllSpecializations(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "50") int size,      // large default — dropdowns need all
            @RequestParam(defaultValue = "specializationName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(doctorSpecializationService.getAllSpecializations(pageable));
    }

    // GET /api/doctor-specializations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DoctorSpecializationResponse> getSpecializationById(
            @PathVariable Long id) {
        return ResponseEntity.ok(doctorSpecializationService.getSpecializationById(id));
    }

    // POST /api/doctor-specializations
    // Kept from original
    @PostMapping
    public ResponseEntity<DoctorSpecializationResponse> addSpecialization(
            @RequestBody DoctorSpecializationRequest request) {
        return ResponseEntity.ok(doctorSpecializationService.addSpecialization(request));
    }

    // PATCH /api/doctor-specializations/{id}
    // Kept from original (was @PatchMapping)
    @PatchMapping("/{id}")
    public ResponseEntity<DoctorSpecializationResponse> updateSpecialization(
            @PathVariable Long id,
            @RequestBody DoctorSpecializationRequest request) {
        return ResponseEntity.ok(doctorSpecializationService.updateSpecialization(id, request));
    }

    // DELETE /api/doctor-specializations/{id}
    // Kept from original
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSpecialization(@PathVariable Long id) {
        return ResponseEntity.ok(doctorSpecializationService.deleteSpecialization(id));
    }
}
