package nidanpro_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTestParameterRequest(
    @NotBlank String parameterName,
    @NotBlank String unit,
    @NotBlank String referenceRange) {
}
