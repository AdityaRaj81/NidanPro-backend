package nidanpro_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record GenerateReceiptRequest(
    String patientCode,
    String patientPhone,
    String patientName,
    @NotEmpty List<Long> testIds) {
}
