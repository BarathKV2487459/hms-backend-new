package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.dto.request.ChangePasswordRequest;
import com.symmetricsquad.hms_backend.dto.request.DoctorRequest;
import com.symmetricsquad.hms_backend.dto.response.AppointmentResponse;
import com.symmetricsquad.hms_backend.dto.response.DoctorResponse;
import com.symmetricsquad.hms_backend.dto.response.PatientResponse;
import com.symmetricsquad.hms_backend.exception.ResourceNotFoundException;
import com.symmetricsquad.hms_backend.mapper.AppointmentMapper;
import com.symmetricsquad.hms_backend.mapper.PatientMapper;
import com.symmetricsquad.hms_backend.model.*;

import com.symmetricsquad.hms_backend.model.enums.AppointmentStatus;
import com.symmetricsquad.hms_backend.model.enums.UserRole;
import com.symmetricsquad.hms_backend.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorSpecializationRepository specializationRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public DoctorResponse addDoctor(DoctorRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceNotFoundException("Email already registered: " + request.getEmail());
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResourceNotFoundException("Passwords do not match");
        }

        DoctorSpecialization spec = findSpecializationById(request.getSpecializationId());

        // Create the User account
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setRole(UserRole.DOCTOR);
        userRepository.save(user);

        // Create the DoctorProfile
        DoctorProfile profile = new DoctorProfile();
        profile.setUser(user);
        profile.setSpecialization(spec);
        profile.setConsultancyFees(request.getConsultancyFees());
        profile.setBio(request.getBio());
        profile.setLicenseNumber(request.getLicenseNumber());
        doctorProfileRepository.save(profile);

        return toResponse(profile);
    }

    @Override
    public Page<DoctorResponse> getAllDoctors(Pageable pageable) {
        return doctorProfileRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public DoctorResponse getDoctorById(Long id) {
        return toResponse(findDoctorProfileById(id));
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
        DoctorProfile profile = findDoctorProfileById(id);
        User user = profile.getUser();

        // Update User fields
        if (request.getFullName()  != null) user.setFullName(request.getFullName());
        if (request.getPhone()     != null) user.setPhone(request.getPhone());
        if (request.getAddress()   != null) user.setAddress(request.getAddress());
        if (request.getGender()    != null) user.setGender(request.getGender());
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResourceNotFoundException("Email already in use: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        userRepository.save(user);

        // Update DoctorProfile fields
        if (request.getSpecializationId() != null) {
            profile.setSpecialization(findSpecializationById(request.getSpecializationId()));
        }
        if (request.getConsultancyFees() != null) profile.setConsultancyFees(request.getConsultancyFees());
        if (request.getBio()             != null) profile.setBio(request.getBio());
        if (request.getLicenseNumber()   != null) profile.setLicenseNumber(request.getLicenseNumber());

        return toResponse(doctorProfileRepository.save(profile));
    }

    @Override
    @Transactional
    public String deleteDoctor(Long id) {
        DoctorProfile profile = findDoctorProfileById(id);

        // Soft-delete future (non-terminal) appointments
        EnumSet<AppointmentStatus> terminal = EnumSet.of(
            AppointmentStatus.COMPLETED,
            AppointmentStatus.CANCELLED_BY_USER,
            AppointmentStatus.CANCELLED_BY_DOCTOR,
            AppointmentStatus.NO_SHOW
        );
        appointmentRepository
            .findByDoctorAndAppointmentDateGreaterThanEqualAndStatusNotIn(
                profile, LocalDate.now(), terminal)
            .forEach(a -> {
                a.softDelete();
                appointmentRepository.save(a);
            });

        profile.softDelete();
        doctorProfileRepository.save(profile);

        profile.getUser().softDelete();
        userRepository.save(profile.getUser());

        return "Doctor deleted successfully";
    }

    @Override
    @Transactional
    public void changePassword(Long doctorId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResourceNotFoundException("New password and confirm password do not match");
        }

        DoctorProfile profile = findDoctorProfileById(doctorId);
        User user = profile.getUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public Page<AppointmentResponse> getUpcomingAppointments(Long doctorId, Pageable pageable) {
        DoctorProfile profile = findDoctorProfileById(doctorId);
        return appointmentRepository
            .findByDoctorAndAppointmentDateGreaterThanEqualAndStatus(
                profile, LocalDate.now(), AppointmentStatus.CONFIRMED, pageable)
            .map(AppointmentMapper::toResponse);
    }

    @Override
    public Page<PatientResponse> getPatientsByDoctor(Long doctorId, Pageable pageable) {
        DoctorProfile profile = findDoctorProfileById(doctorId);
        return patientProfileRepository
            .findByAssignedDoctor(profile, pageable)
            .map(PatientMapper::toResponse);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private DoctorProfile findDoctorProfileById(Long id) {
        return doctorProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    private DoctorSpecialization findSpecializationById(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found with id: " + id));
    }

    private DoctorResponse toResponse(DoctorProfile profile) {
        User user = profile.getUser();
        DoctorResponse res = new DoctorResponse();
        res.setUserId(user.getId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setDoctorProfileId(profile.getId());
        res.setSpecializationName(profile.getSpecialization().getSpecializationName());
        res.setConsultancyFees(profile.getConsultancyFees());
        res.setBio(profile.getBio());
        res.setLicenseNumber(profile.getLicenseNumber());
        res.setCreatedAt(profile.getCreatedAt());
        res.setUpdatedAt(profile.getUpdatedAt());
        return res;
    }
}
