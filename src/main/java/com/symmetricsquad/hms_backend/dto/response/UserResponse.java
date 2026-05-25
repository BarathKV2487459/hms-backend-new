package com.symmetricsquad.hms_backend.dto.response;

import com.symmetricsquad.hms_backend.model.enums.Gender;
import com.symmetricsquad.hms_backend.model.enums.UserRole;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Gender gender;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}