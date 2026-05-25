package com.symmetricsquad.hms_backend.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    private Long userId;
    private Long doctorId;
    private LocalDate appointmentDate;
    private LocalTime startTime;    // was String appointmentTime — now typed + split into start/end
    private LocalTime endTime;
}