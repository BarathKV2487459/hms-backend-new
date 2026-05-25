package com.symmetricsquad.hms_backend.dto.response;

import com.symmetricsquad.hms_backend.model.enums.AppointmentStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class AppointmentResponse {
    private Long id;

    // Patient summary
    private Long userId;
    private String patientName;
    private String patientEmail;

    // Doctor summary
    private Long doctorProfileId;
    private String doctorName;
    private String specializationName;

    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long consultancyFees;

    private AppointmentStatus status;
    private String cancellationReason;  // null unless cancelled

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}