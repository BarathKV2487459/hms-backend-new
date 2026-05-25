package com.symmetricsquad.hms_backend.dto.response;

// Replaces UpdatedDoctorResponse — flattens User + DoctorProfile into one response

import com.symmetricsquad.hms_backend.model.enums.Gender;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DoctorResponse {
    // From User
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Gender gender;

    // From DoctorProfile
    private Long doctorProfileId;
    private String specializationName;
    private Long consultancyFees;
    private String bio;
    private String licenseNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}