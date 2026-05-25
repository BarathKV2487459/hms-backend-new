package com.symmetricsquad.hms_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "doctor_profiles")
@SQLRestriction("is_active = true")
public class DoctorProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Back to the User account (source of name, email, password, phone)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id", nullable = false)
    private DoctorSpecialization specialization;

    private Long consultancyFees;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String licenseNumber;

    // Patients currently assigned to this doctor
    @OneToMany(mappedBy = "assignedDoctor", fetch = FetchType.LAZY)
    private List<PatientProfile> patients = new ArrayList<>();

    // Appointments assigned to this doctor
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "recordedBy", fetch = FetchType.LAZY)
    private List<MedicalHistory> medicalHistories = new ArrayList<>();
}
