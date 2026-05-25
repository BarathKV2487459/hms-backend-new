package com.symmetricsquad.hms_backend.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    // Changed from username → email to match User model (email is the unique identifier)
    private String email;
    private String password;
}