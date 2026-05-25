package com.symmetricsquad.hms_backend.mapper;

import com.symmetricsquad.hms_backend.model.Appointment;
import com.symmetricsquad.hms_backend.dto.response.AppointmentResponse;

public class AppointmentMapper {

    private AppointmentMapper() {}  // utility class — no instantiation

    public static AppointmentResponse toResponse(Appointment a) {
        AppointmentResponse res = new AppointmentResponse();
        res.setId(a.getId());
        res.setUserId(a.getUser().getId());
        res.setPatientName(a.getUser().getFullName());
        res.setPatientEmail(a.getUser().getEmail());
        res.setDoctorProfileId(a.getDoctor().getId());
        res.setDoctorName(a.getDoctor().getUser().getFullName());
        res.setSpecializationName(a.getDoctorSpecialization().getSpecializationName());
        res.setAppointmentDate(a.getAppointmentDate());
        res.setStartTime(a.getStartTime());
        res.setEndTime(a.getEndTime());
        res.setConsultancyFees(a.getConsultancyFees());
        res.setStatus(a.getStatus());
        res.setCancellationReason(a.getCancellationReason());
        res.setCreatedAt(a.getCreatedAt());
        res.setUpdatedAt(a.getUpdatedAt());
        return res;
    }
}