package com.symmetricsquad.hms_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient_profiles")
@SQLRestriction("is_active = true")
public class PatientProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Back to the User account
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Assigned primary doctor (set after first appointment)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_doctor_id")
    private DoctorProfile assignedDoctor;

    private LocalDate dateOfBirth;

    @Column(columnDefinition = "TEXT")
    private String knownAllergies;

    @Column(columnDefinition = "TEXT")
    private String existingConditions;

    @Column(columnDefinition = "TEXT")
    private String currentMedications;

    // Full medical visit history
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MedicalHistory> medicalHistories = new ArrayList<>();
}
