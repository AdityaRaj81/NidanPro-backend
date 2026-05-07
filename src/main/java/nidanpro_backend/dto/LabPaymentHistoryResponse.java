package nidanpro_backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record LabPaymentHistoryResponse(
    Long id,
    Long labId,
    String labName,
    String subscriptionPlan,
    BigDecimal amount,
    String paymentStatus,
    LocalDate periodStart,
    LocalDate periodEnd,
    Instant paidAt,
    String remarks,
    Instant createdAt) {
}
