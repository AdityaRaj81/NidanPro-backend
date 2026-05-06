package nidanpro_backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import nidanpro_backend.model.Gender;

public record CreatePatientRequest(
        @NotBlank String fullName,
        @NotBlank String phoneNumber,
        LocalDate dateOfBirth,
        Integer ageYears,
        Integer ageMonths,
        Integer ageDays,
        Gender gender,
        String address) {
}
