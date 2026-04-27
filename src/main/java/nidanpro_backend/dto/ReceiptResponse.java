package nidanpro_backend.dto;

import java.time.Instant;
import java.util.List;

public record ReceiptResponse(
    String receiptCode,
    Instant generatedAt,
    String patientRef,
    String patientName,
    List<String> tests) {
}
