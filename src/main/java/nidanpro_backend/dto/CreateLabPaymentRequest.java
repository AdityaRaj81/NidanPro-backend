package nidanpro_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateLabPaymentRequest(
    String subscriptionPlan,
    BigDecimal amount,
    String paymentStatus,
    LocalDate periodStart,
    LocalDate periodEnd,
    String remarks) {
}
