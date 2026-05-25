package com.symmetricsquad.hms_backend.dto.request;

import lombok.Data;
import java.util.Map;

@Data
public class MedicalHistoryRequest {

    /**
     * Free-form vitals — any key-value pairs the doctor wants to record.
     * Common keys: "bloodPressure", "bloodSugar", "weight",
     *              "temperature", "pulseRate", "oxygenSaturation"
     *
     * Example JSON body:
     * {
     *   "vitals": {
     *     "bloodPressure": "120/80",
     *     "weight": "70 kg",
     *     "pulseRate": "72 bpm"
     *   },
     *   "clinicalData": {
     *     "symptoms": "Fever, fatigue",
     *     "diagnosis": "Viral fever",
     *     "prescription": "Paracetamol 500mg TID x 5 days",
     *     "followUpDate": "2025-06-01"
     *   }
     * }
     */
    private Map<String, Object> vitals;
    private Map<String, Object> clinicalData;
}