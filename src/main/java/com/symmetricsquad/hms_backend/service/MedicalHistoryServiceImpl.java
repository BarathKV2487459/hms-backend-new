package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.exception.ResourceNotFoundException;
import com.symmetricsquad.hms_backend.model.Appointment;
import com.symmetricsquad.hms_backend.model.DoctorProfile;
import com.symmetricsquad.hms_backend.model.MedicalHistory;
import com.symmetricsquad.hms_backend.model.PatientProfile;
import com.symmetricsquad.hms_backend.dto.request.MedicalHistoryRequest;
import com.symmetricsquad.hms_backend.dto.response.MedicalHistoryResponse;
import com.symmetricsquad.hms_backend.model.enums.AppointmentStatus;
import com.symmetricsquad.hms_backend.repository.AppointmentRepository;
import com.symmetricsquad.hms_backend.repository.DoctorProfileRepository;
import com.symmetricsquad.hms_backend.repository.MedicalHistoryRepository;
import com.symmetricsquad.hms_backend.repository.PatientProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    @Override
    @Transactional
    public MedicalHistoryResponse createRecord(Long patientId, Long doctorId,
                                               MedicalHistoryRequest request) {
        PatientProfile patient = findPatientById(patientId);
        DoctorProfile doctor   = findDoctorById(doctorId);

        MedicalHistory record = new MedicalHistory();
        record.setPatient(patient);
        record.setRecordedBy(doctor);
        record.setVitals(request.getVitals());
        record.setClinicalData(request.getClinicalData());

        return toResponse(medicalHistoryRepository.save(record));
    }

    @Override
    public MedicalHistoryResponse getRecordById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public Page<MedicalHistoryResponse> getRecordsByPatient(Long patientId, Pageable pageable) {
        PatientProfile patient = findPatientById(patientId);
        return medicalHistoryRepository
            .findByPatient(patient, pageable)
            .map(this::toResponse);
    }

    @Override
    public Page<MedicalHistoryResponse> getRecordsByDoctor(Long doctorId, Pageable pageable) {
        DoctorProfile doctor = findDoctorById(doctorId);
        return medicalHistoryRepository
            .findByRecordedBy(doctor, pageable)
            .map(this::toResponse);
    }

    @Override
    @Transactional
    public MedicalHistoryResponse updateRecord(Long id, Long doctorId,
                                               MedicalHistoryRequest request) {
        MedicalHistory record = findById(id);

        // Only the doctor who created the record can update it
        if (!record.getRecordedBy().getId().equals(doctorId)) {
            throw new ResourceNotFoundException("You can only update records you created");
        }

        if (request.getVitals()      != null) record.setVitals(request.getVitals());
        if (request.getClinicalData() != null) record.setClinicalData(request.getClinicalData());

        return toResponse(medicalHistoryRepository.save(record));
    }

    @Override
    @Transactional
    public String deleteRecord(Long id) {
        MedicalHistory record = findById(id);
        record.softDelete();
        medicalHistoryRepository.save(record);
        return "Medical history record deleted successfully";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private MedicalHistory findById(Long id) {
        return medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Medical history record not found with id: " + id));
    }

    private PatientProfile findPatientById(Long id) {
        return patientProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Patient not found with id: " + id));
    }

    private DoctorProfile findDoctorById(Long id) {
        return doctorProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Doctor not found with id: " + id));
    }


    private MedicalHistoryResponse toResponse(MedicalHistory record) {
        MedicalHistoryResponse res = new MedicalHistoryResponse();
        res.setId(record.getId());
        res.setPatientProfileId(record.getPatient().getId());
        res.setPatientName(record.getPatient().getUser().getFullName());
        res.setDoctorProfileId(record.getRecordedBy().getId());
        res.setRecordedByDoctorName(record.getRecordedBy().getUser().getFullName());
        res.setVitals(record.getVitals());
        res.setClinicalData(record.getClinicalData());
        res.setCreatedAt(record.getCreatedAt());
        res.setUpdatedAt(record.getUpdatedAt());
        return res;
    }
}
