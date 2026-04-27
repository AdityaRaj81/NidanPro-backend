package nidanpro_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(
    @NotBlank String phoneNumber,
    @NotBlank String otp) {
}
