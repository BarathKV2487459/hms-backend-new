package com.symmetricsquad.hms_backend.dto.response;

import com.symmetricsquad.hms_backend.model.enums.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientResponse {
    // From User
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Gender gender;

    // From PatientProfile
    private Long patientProfileId;
    private LocalDate dateOfBirth;
    private String knownAllergies;
    private String existingConditions;
    private String currentMedications;

    // Assigned doctor summary (avoid nesting full DoctorResponse)
    private Long assignedDoctorProfileId;
    private String assignedDoctorName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}