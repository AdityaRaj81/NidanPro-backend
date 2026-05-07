package nidanpro_backend.dto;

import java.time.Instant;
import java.time.LocalDate;

public record LabSummaryResponse(
        Long id,
        String labName,
        String labNumber,
        String subdomain,
        String customDomain,
        String subscriptionPlan,
        LocalDate subscriptionExpiry,
        String paymentStatus,
        boolean active,
        Instant createdAt,
        long branchCount) {
}
