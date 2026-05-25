package com.symmetricsquad.hms_backend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class MedicalHistoryResponse {
    private Long id;

    // Patient summary
    private Long patientProfileId;
    private String patientName;

    // Doctor who recorded it
    private Long doctorProfileId;
    private String recordedByDoctorName;

    // Free-form data — Angular iterates these with the keyvalue pipe
    private Map<String, Object> vitals;
    private Map<String, Object> clinicalData;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}