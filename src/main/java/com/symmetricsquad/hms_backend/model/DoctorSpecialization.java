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
@Table(name = "doctor_specializations")
@SQLRestriction("is_active = true")
public class DoctorSpecialization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String specializationName;

    private String description;

    @OneToMany(mappedBy = "specialization", fetch = FetchType.LAZY)
    private List<DoctorProfile> doctors = new ArrayList<>();

    @OneToMany(mappedBy = "doctorSpecialization", fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();
}
