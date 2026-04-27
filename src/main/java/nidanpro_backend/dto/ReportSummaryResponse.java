package nidanpro_backend.dto;

import java.time.Instant;
import nidanpro_backend.model.ReportStatus;

public record ReportSummaryResponse(
    Long id,
    String reportCode,
    String patientName,
    String testName,
    ReportStatus status,
    Instant createdAt) {
}
