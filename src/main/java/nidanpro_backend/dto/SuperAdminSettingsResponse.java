package nidanpro_backend.dto;

public record SuperAdminSettingsResponse(
    long totalLabs,
    long activeLabs,
    long suspendedLabs,
    long totalBranches,
    long totalLabUsers,
    long basicPlans,
    long standardPlans,
    long proPlans,
    long enterprisePlans,
    long paidLabs,
    long pendingLabs,
    long overdueLabs,
    long suspendedPayments) {
}
