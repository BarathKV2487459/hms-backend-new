package com.symmetricsquad.hms_backend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DoctorSpecializationResponse {
    private Long id;
    private String specializationName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}