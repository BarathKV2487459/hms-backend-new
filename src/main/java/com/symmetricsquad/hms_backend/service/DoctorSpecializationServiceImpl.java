package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.exception.ResourceNotFoundException;
import com.symmetricsquad.hms_backend.model.DoctorSpecialization;
import com.symmetricsquad.hms_backend.dto.request.DoctorSpecializationRequest;
import com.symmetricsquad.hms_backend.dto.response.DoctorSpecializationResponse;
import com.symmetricsquad.hms_backend.repository.DoctorSpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorSpecializationServiceImpl implements DoctorSpecializationService {

    private final DoctorSpecializationRepository specializationRepository;

    @Override
    @Transactional
    public DoctorSpecializationResponse addSpecialization(DoctorSpecializationRequest request) {
        if (specializationRepository.existsBySpecializationNameIgnoreCase(request.getSpecializationName())) {
            throw new ResourceNotFoundException(
                "Specialization already exists: " + request.getSpecializationName());
        }

        DoctorSpecialization spec = new DoctorSpecialization();
        spec.setSpecializationName(request.getSpecializationName());
        spec.setDescription(request.getDescription());

        return toResponse(specializationRepository.save(spec));
    }

    @Override
    public Page<DoctorSpecializationResponse> getAllSpecializations(Pageable pageable) {
        return specializationRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public DoctorSpecializationResponse getSpecializationById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional
    public DoctorSpecializationResponse updateSpecialization(Long id, DoctorSpecializationRequest request) {
        DoctorSpecialization spec = findById(id);

        if (request.getSpecializationName() != null) {
            spec.setSpecializationName(request.getSpecializationName());
        }
        if (request.getDescription() != null) {
            spec.setDescription(request.getDescription());
        }

        return toResponse(specializationRepository.save(spec));
    }

    @Override
    @Transactional
    public String deleteSpecialization(Long id) {
        DoctorSpecialization spec = findById(id);
        spec.softDelete();
        specializationRepository.save(spec);
        return "Specialization deleted successfully";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private DoctorSpecialization findById(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Specialization not found with id: " + id));
    }

    private DoctorSpecializationResponse toResponse(DoctorSpecialization spec) {
        DoctorSpecializationResponse res = new DoctorSpecializationResponse();
        res.setId(spec.getId());
        res.setSpecializationName(spec.getSpecializationName());
        res.setDescription(spec.getDescription());
        res.setCreatedAt(spec.getCreatedAt());
        res.setUpdatedAt(spec.getUpdatedAt());
        return res;
    }
}
