package nidanpro_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStaffRequest(
    @NotBlank String fullName,
    @Email @NotBlank String email,
    @NotBlank String password,
    @NotNull String role,
    String phone,
    String signatureUrl,
    boolean active) {
}
