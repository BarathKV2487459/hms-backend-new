package com.symmetricsquad.hms_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "medical_histories")
@SQLRestriction("is_active = true")
public class MedicalHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which patient this record belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

//    // Exactly which appointment generated this record
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
//    private Appointment appointment;

    // Which doctor recorded this entry
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_doctor_id", nullable = false)
    private DoctorProfile recordedBy;

    /**
     * Dynamic vitals — any measurable value as key-value pairs.
     * MySQL JSON column; queryable with JSON_EXTRACT() if needed.
     *
     * Example:
     * {
     *   "bloodPressure": "120/80",
     *   "pulseRate": "72 bpm",
     *   "weight": "70 kg",
     *   "oxygenSaturation": "98%",
     *   "temperature": "98.6 F"
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "vitals", columnDefinition = "JSON")
    private Map<String, Object> vitals = new HashMap<>();

    /**
     * Clinical assessment — diagnosis, prescription, notes, follow-up, etc.
     * Kept separate from vitals so Angular renders two distinct UI sections.
     *
     * Example:
     * {
     *   "symptoms": "Fever, headache, fatigue",
     *   "diagnosis": "Viral fever",
     *   "prescription": "Paracetamol 500mg TID x 5 days",
     *   "doctorNotes": "Rest advised. Follow up in 5 days.",
     *   "followUpDate": "2025-06-01",
     *   "referredTo": "Cardiologist"
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "clinical_data", columnDefinition = "JSON")
    private Map<String, Object> clinicalData = new HashMap<>();
}
