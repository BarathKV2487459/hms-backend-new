package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.exception.ResourceNotFoundException;
import com.symmetricsquad.hms_backend.mapper.PatientMapper;
import com.symmetricsquad.hms_backend.model.DoctorProfile;
import com.symmetricsquad.hms_backend.model.PatientProfile;
import com.symmetricsquad.hms_backend.model.User;
import com.symmetricsquad.hms_backend.dto.request.PatientProfileRequest;
import com.symmetricsquad.hms_backend.dto.response.PatientResponse;
import com.symmetricsquad.hms_backend.repository.DoctorProfileRepository;
import com.symmetricsquad.hms_backend.repository.PatientProfileRepository;
import com.symmetricsquad.hms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    @Override
    @Transactional
    public PatientResponse createPatientProfile(Long userId, PatientProfileRequest request) {
        User user = findUserById(userId);

        if (patientProfileRepository.findByUser(user).isPresent()) {
            throw new IllegalStateException("Patient profile already exists for user: " + userId);
        }

        PatientProfile profile = new PatientProfile();
        profile.setUser(user);
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setKnownAllergies(request.getKnownAllergies());
        profile.setExistingConditions(request.getExistingConditions());
        profile.setCurrentMedications(request.getCurrentMedications());

        if (request.getAssignedDoctorId() != null) {
            profile.setAssignedDoctor(findDoctorById(request.getAssignedDoctorId()));
        }

        return toResponse(patientProfileRepository.save(profile));
    }

    @Override
    public PatientResponse getPatientById(Long id) {
        return toResponse(findProfileById(id));
    }

    @Override
    public PatientResponse getPatientByUserId(Long userId) {
        User user = findUserById(userId);
        PatientProfile profile = patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No patient profile found for user id: " + userId));
        return toResponse(profile);
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(Long id, PatientProfileRequest request) {
        PatientProfile profile = findProfileById(id);

        if (request.getDateOfBirth()                != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getKnownAllergies()     != null) profile.setKnownAllergies(request.getKnownAllergies());
        if (request.getExistingConditions() != null) profile.setExistingConditions(request.getExistingConditions());
        if (request.getCurrentMedications() != null) profile.setCurrentMedications(request.getCurrentMedications());

        if (request.getAssignedDoctorId() != null) {
            profile.setAssignedDoctor(findDoctorById(request.getAssignedDoctorId()));
        }

        return toResponse(patientProfileRepository.save(profile));
    }

    @Override
    public Page<PatientResponse> getAllPatients(Pageable pageable) {
        return patientProfileRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public String deletePatient(Long id) {
        PatientProfile profile = findProfileById(id);
        profile.softDelete();
        patientProfileRepository.save(profile);
        return "Patient profile deleted successfully";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private PatientProfile findProfileById(Long id) {
        return patientProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Patient profile not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User not found with id: " + id));
    }

    private DoctorProfile findDoctorById(Long id) {
        return doctorProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Doctor not found with id: " + id));
    }

    @Override
    public Page<PatientResponse> getPatientsByDoctor(Long doctorId, Pageable pageable) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + doctorId));
        return patientProfileRepository
                .findByAssignedDoctor(doctor, pageable)
                .map(PatientMapper::toResponse);
    }

    PatientResponse toResponse(PatientProfile profile) {
        User user = profile.getUser();
        PatientResponse res = new PatientResponse();
        res.setUserId(user.getId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setPatientProfileId(profile.getId());
        res.setDateOfBirth(profile.getDateOfBirth());
        res.setKnownAllergies(profile.getKnownAllergies());
        res.setExistingConditions(profile.getExistingConditions());
        res.setCurrentMedications(profile.getCurrentMedications());

        if (profile.getAssignedDoctor() != null) {
            res.setAssignedDoctorProfileId(profile.getAssignedDoctor().getId());
            res.setAssignedDoctorName(profile.getAssignedDoctor().getUser().getFullName());
        }

        res.setCreatedAt(profile.getCreatedAt());
        res.setUpdatedAt(profile.getUpdatedAt());
        return res;
    }
}
