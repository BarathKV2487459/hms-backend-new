package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.dto.request.AppointmentRequest;
import com.symmetricsquad.hms_backend.dto.response.AppointmentResponse;
import com.symmetricsquad.hms_backend.exception.ConflictException;
import com.symmetricsquad.hms_backend.exception.ForbiddenException;
import com.symmetricsquad.hms_backend.exception.ResourceNotFoundException;
import com.symmetricsquad.hms_backend.model.*;
import com.symmetricsquad.hms_backend.model.enums.*;
import com.symmetricsquad.hms_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorSpecializationRepository specializationRepository;
    private final PatientProfileRepository patientProfileRepository;

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User not found with id: " + request.getUserId()));

        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Doctor not found with id: " + request.getDoctorId()));

        // Slot overlap validation
        if (!isSlotAvailable(doctor.getId(), request.getAppointmentDate(),
                request.getStartTime(), request.getEndTime())) {
            throw new ConflictException("Doctor already has an appointment in this time slot");
        }

        // Auto-create PatientProfile on first booking
        if (patientProfileRepository.findByUser(user).isEmpty()) {
            PatientProfile profile = new PatientProfile();
            profile.setUser(user);
            profile.setAssignedDoctor(doctor);
            patientProfileRepository.save(profile);
        }

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setDoctor(doctor);
        appointment.setDoctorSpecialization(doctor.getSpecialization());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setConsultancyFees(doctor.getConsultancyFees());
        appointment.setStatus(AppointmentStatus.PENDING);

        return toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public Page<AppointmentResponse> getAppointments(
            Long patientUserId,
            Long doctorId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            AppointmentStatus status,
            Pageable pageable) {

        // Build a dynamic Specification from whichever filters are non-null
        Specification<Appointment> spec = Specification.where((Specification<Appointment>) null);

        if (patientUserId != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("user").get("id"), patientUserId));
        }
        if (doctorId != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("doctor").get("id"), doctorId));
        }
        if (date != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("appointmentDate"), date));
        }
        if (startTime != null) {
            spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("startTime"), startTime));
        }
        if (endTime != null) {
            spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("endTime"), endTime));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), status));
        }
        System.out.println(spec);
        return appointmentRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        System.out.println(toResponse(findById(id)));
        return toResponse(findById(id));
    }

    @Override
    @Transactional
    public String cancelByUser(Long appointmentId, Long userId) {
        Appointment appointment = findById(appointmentId);

        if (!appointment.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("You can only cancel your own appointments");
        }

        EnumSet<AppointmentStatus> cancellable = EnumSet.of(
            AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED);
        if (!cancellable.contains(appointment.getStatus())) {
            throw new IllegalStateException(
                "Cannot cancel appointment with status: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CANCELLED_BY_USER);
        appointmentRepository.save(appointment);
        return "Appointment cancelled successfully";
    }

    @Override
    @Transactional
    public String cancelByDoctor(Long appointmentId, Long doctorId) {
        Appointment appointment = findById(appointmentId);

        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new ResourceNotFoundException("You can only cancel your own appointments");
        }

        EnumSet<AppointmentStatus> cancellable = EnumSet.of(
            AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED);
        if (!cancellable.contains(appointment.getStatus())) {
            throw new IllegalStateException(
                "Cannot cancel appointment with status: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CANCELLED_BY_DOCTOR);
        appointmentRepository.save(appointment);
        return "Appointment cancelled successfully";
    }

    @Override
    public String confirmAppointment(Long appointmentId, Long doctorId) {
        Appointment appointment = findById(appointmentId);

        if(!appointment.getDoctor().getId().equals(doctorId)){
            throw new ForbiddenException("You can only confirm your own appointments");
        }

        if(appointment.getStatus() != AppointmentStatus.PENDING){
            throw new ConflictException("Only PENDING appointments can be marked as completed");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);
        return "Appointment marked as confirmed";
    }

    @Override
    @Transactional
    public String completeAppointment(Long appointmentId, Long doctorId) {
        Appointment appointment = findById(appointmentId);

        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new ForbiddenException("You can only complete your own appointments");
        }

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new ConflictException(
                "Only CONFIRMED appointments can be marked as completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        return "Appointment marked as completed";
    }

    @Override
    public boolean isSlotAvailable(Long doctorId, LocalDate date,
                                   LocalTime startTime, LocalTime endTime) {
        return !appointmentRepository.existsOverlappingAppointment(
            doctorId, date, startTime, endTime,
            EnumSet.of(
                AppointmentStatus.CANCELLED_BY_USER,
                AppointmentStatus.CANCELLED_BY_DOCTOR,
                AppointmentStatus.NO_SHOW
            )
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Appointment not found with id: " + id));
    }

    AppointmentResponse toResponse(Appointment a) {
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
