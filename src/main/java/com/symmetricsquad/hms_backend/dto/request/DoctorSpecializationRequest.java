package com.symmetricsquad.hms_backend.dto.request;

import lombok.Data;

@Data
public class DoctorSpecializationRequest {
    private String specializationName;
    private String description;
}