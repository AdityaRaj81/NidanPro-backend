package nidanpro_backend.dto;

import nidanpro_backend.model.Gender;

public record PatientSummaryResponse(
    Long id,
    String patientCode,
    String fullName,
    Integer age,
    Gender gender,
    String phoneNumber) {
}
