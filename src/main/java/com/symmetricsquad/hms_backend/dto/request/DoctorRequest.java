package com.symmetricsquad.hms_backend.dto.request;

import com.symmetricsquad.hms_backend.model.enums.Gender;
import lombok.Data;

@Data
public class DoctorRequest {
    private Long specializationId;
    private String fullName;         // stored in User
    private String email;            // stored in User
    private String password;         // stored in User — only required on add, ignored on update
    private String confirmPassword;  // only validated on add
    private Gender gender;           // stored in User
    private String phone;            // stored in User
    private String address;          // stored in User
    private Long consultancyFees;    // stored in DoctorProfile
    private String bio;              // stored in DoctorProfile
    private String licenseNumber;    // stored in DoctorProfile
}