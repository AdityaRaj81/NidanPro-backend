package nidanpro_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import nidanpro_backend.model.Gender;

public record CreatePatientRequest(
    @NotBlank String fullName,
    @NotBlank String phoneNumber,
    @NotNull LocalDate dateOfBirth,
    @NotNull Gender gender,
    String address) {
}
