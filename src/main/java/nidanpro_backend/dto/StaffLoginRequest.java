package nidanpro_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record StaffLoginRequest(
    @NotBlank String loginId,
    @NotBlank String password,
    String role) {
}
