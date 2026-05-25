package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.dto.response.*;
import com.symmetricsquad.hms_backend.exception.ResourceNotFoundException;
import com.symmetricsquad.hms_backend.mapper.AppointmentMapper;
import com.symmetricsquad.hms_backend.mapper.PatientMapper;
import com.symmetricsquad.hms_backend.model.Appointment;
import com.symmetricsquad.hms_backend.model.ContactQuery;
import com.symmetricsquad.hms_backend.model.DoctorProfile;
import com.symmetricsquad.hms_backend.model.User;
import com.symmetricsquad.hms_backend.model.enums.AppointmentStatus;
import com.symmetricsquad.hms_backend.model.enums.UserRole;
import com.symmetricsquad.hms_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AppointmentRepository appointmentRepository;
    private final ContactQueryRepository contactQueryRepository;
    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;

    // ── Appointment ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public String markNoShow(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Appointment not found with id: " + appointmentId));

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException(
                "Only CONFIRMED appointments can be marked as NO_SHOW");
        }

        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointmentRepository.save(appointment);
        return "Appointment marked as NO_SHOW";
    }

    @Override
    public Page<AppointmentResponse> getAllAppointmentsAdmin(Pageable pageable) {
        // No status filter — admin sees everything including NO_SHOW
        return appointmentRepository.findAll(pageable)
            .map(this::toAppointmentResponse);
    }

    // ── Contact Queries ───────────────────────────────────────────────────────

    @Override
    public Page<ContactQueryResponse> getAllContactQueries(Boolean isRead, Pageable pageable) {
        if (isRead != null) {
            return contactQueryRepository.findByIsRead(isRead, pageable)
                .map(this::toContactQueryResponse);
        }
        return contactQueryRepository.findAll(pageable)
            .map(this::toContactQueryResponse);
    }

    @Override
    public ContactQueryResponse getContactQueryById(Long id) {
        return toContactQueryResponse(findContactQueryById(id));
    }

    @Override
    @Transactional
    public ContactQueryResponse respondToContactQuery(Long id, String remark) {
        ContactQuery query = findContactQueryById(id);
        query.setAdminRemark(remark);
        query.setIsRead(true);
        return toContactQueryResponse(contactQueryRepository.save(query));
    }

    @Override
    @Transactional
    public String deleteContactQuery(Long id) {
        ContactQuery query = findContactQueryById(id);
        query.softDelete();
        contactQueryRepository.save(query);
        return "Contact query deleted successfully";
    }

    // ── User & Doctor oversight ───────────────────────────────────────────────

    @Override
    public Page<UserResponse> getAllUsers(boolean includeInactive, Pageable pageable) {
        if (includeInactive) {
            // Use a native/JPQL query that bypasses the @Where(is_active=true) filter
            return userRepository.findAllIncludingInactive(pageable)
                .map(this::toUserResponse);
        }
        return userRepository.findAll(pageable).map(this::toUserResponse);
    }

    @Override
    public Page<DoctorResponse> getAllDoctors(boolean includeInactive, Pageable pageable) {
        if (includeInactive) {
            return doctorProfileRepository.findAllIncludingInactive(pageable)
                .map(this::toDoctorResponse);
        }
        return doctorProfileRepository.findAll(pageable).map(this::toDoctorResponse);
    }

    @Override
    public AdminDashboardResponse getDashboard(int recentSize) {
        AdminDashboardResponse dashboard = new AdminDashboardResponse();
        Pageable topN = PageRequest.of(0, recentSize);

        // ── Flat counts ───────────────────────────────────────────────────────────

        dashboard.setTotalPatients(userRepository.countByRole(UserRole.PATIENT));
        dashboard.setTotalDoctors(userRepository.countByRole(UserRole.DOCTOR));
        dashboard.setTotalAppointments(appointmentRepository.count());
        dashboard.setTotalMedicalHistoryRecords(medicalHistoryRepository.count());
        dashboard.setOpenContactQueries(contactQueryRepository.countByIsRead(false));

        // ── Doctors grouped by specialization ────────────────────────────────────

        Map<String, Long> bySpec = new LinkedHashMap<>();
        doctorProfileRepository.countGroupedBySpecialization()
                .forEach(row -> bySpec.put((String) row[0], (Long) row[1]));
        dashboard.setDoctorsBySpecialization(bySpec);

        // ── Appointments grouped by status ────────────────────────────────────────

        Map<String, Long> byStatus = new LinkedHashMap<>();
        appointmentRepository.countGroupedByStatus()
                .forEach(row -> byStatus.put(
                        ((AppointmentStatus) row[0]).name(), (Long) row[1]));
        dashboard.setAppointmentsByStatus(byStatus);

        // ── Top-N upcoming appointments (CONFIRMED, today onwards) ───────────────

        List<AppointmentResponse> upcoming =
                appointmentRepository
                        .findByStatusAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscStartTimeAsc(
                                AppointmentStatus.CONFIRMED, LocalDate.now(), topN)
                        .stream()
                        .map(AppointmentMapper::toResponse)
                        .collect(Collectors.toList());
        dashboard.setUpcomingAppointments(upcoming);

        // ── Top-N recently active patients ────────────────────────────────────────

        List<PatientResponse> recentPatients =
                patientProfileRepository.findAllByOrderByUpdatedAtDesc(topN)
                        .stream()
                        .map(PatientMapper::toResponse)
                        .collect(Collectors.toList());
        dashboard.setRecentActivePatients(recentPatients);

        // ── Top-N recently active doctors ─────────────────────────────────────────

        List<DoctorResponse> recentDoctors =
                doctorProfileRepository.findAllByOrderByUpdatedAtDesc(topN)
                        .stream()
                        .map(this::toDoctorResponse)
                        .collect(Collectors.toList());
        dashboard.setRecentActiveDoctors(recentDoctors);

        return dashboard;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private ContactQuery findContactQueryById(Long id) {
        return contactQueryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Contact query not found with id: " + id));
    }

    private AppointmentResponse toAppointmentResponse(Appointment a) {
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

    private ContactQueryResponse toContactQueryResponse(ContactQuery q) {
        ContactQueryResponse res = new ContactQueryResponse();
        res.setId(q.getId());
        res.setFullName(q.getFullName());
        res.setEmail(q.getEmail());
        res.setContactNo(q.getContactNo());
        res.setMessage(q.getMessage());
        res.setAdminRemark(q.getAdminRemark());
        res.setIsRead(q.getIsRead());
        res.setHandledByAdminName(
            q.getHandledBy() != null ? q.getHandledBy().getFullName() : null);
        res.setCreatedAt(q.getCreatedAt());
        res.setUpdatedAt(q.getUpdatedAt());
        return res;
    }

    private UserResponse toUserResponse(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setRole(user.getRole());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

    private DoctorResponse toDoctorResponse(DoctorProfile profile) {
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
