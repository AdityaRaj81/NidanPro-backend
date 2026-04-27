package nidanpro_backend.dto;

import jakarta.validation.constraints.NotNull;

public record CreateReportRequest(
    @NotNull Long patientId,
    @NotNull Long testId) {
}
