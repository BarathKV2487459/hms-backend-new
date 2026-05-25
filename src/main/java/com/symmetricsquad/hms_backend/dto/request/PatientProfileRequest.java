package com.symmetricsquad.hms_backend.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientProfileRequest {
    private Long assignedDoctorId;       // nullable — assigned after first appointment
    private LocalDate dateOfBirth;
    private String knownAllergies;
    private String existingConditions;
    private String currentMedications;
}