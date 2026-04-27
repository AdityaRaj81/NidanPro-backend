package nidanpro_backend.dto;

import java.util.List;

public record VerifyOtpResponse(
    String message,
    List<PatientSummaryResponse> patients) {
}
