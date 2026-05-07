package nidanpro_backend.dto;

import java.time.LocalDate;

public record CreateLabRequest(
        String labName,
        String labNumber,
        String subdomain,
        String customDomain,
        String logoUrl,
        String primaryColor,
        String secondaryColor,
        String subscriptionPlan,
        LocalDate subscriptionExpiry,
        String paymentStatus,
        Boolean active,
        String adminName,
        String adminEmail,
        String adminPassword,
        String adminPhone) {
}
