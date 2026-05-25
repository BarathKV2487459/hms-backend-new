package com.symmetricsquad.hms_backend.dto.request;

import com.symmetricsquad.hms_backend.model.enums.Gender;
import lombok.Data;

@Data
public class UserRequest {
    private String fullName;
    private String email;
    private String password;
    private String address;
    private Gender gender;  // enum, not raw String
}