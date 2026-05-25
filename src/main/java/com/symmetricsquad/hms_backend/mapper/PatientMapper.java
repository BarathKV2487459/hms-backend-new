package com.symmetricsquad.hms_backend.mapper;


import com.symmetricsquad.hms_backend.model.PatientProfile;
import com.symmetricsquad.hms_backend.model.User;
import com.symmetricsquad.hms_backend.dto.response.PatientResponse;

public class PatientMapper {

    private PatientMapper() {}  // utility class — no instantiation

    public static PatientResponse toResponse(PatientProfile profile) {
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
