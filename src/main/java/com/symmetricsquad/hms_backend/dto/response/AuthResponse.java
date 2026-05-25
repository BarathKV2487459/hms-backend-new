package com.symmetricsquad.hms_backend.dto.response;

import com.symmetricsquad.hms_backend.model.enums.UserRole;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private UserRole role;    // enum not String
    private Long userId;
    private String fullName;
    private String email;
}