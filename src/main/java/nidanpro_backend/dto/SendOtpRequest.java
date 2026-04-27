package nidanpro_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record SendOtpRequest(@NotBlank String phoneNumber) {
}
