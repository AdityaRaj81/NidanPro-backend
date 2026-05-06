package nidanpro_backend.dto;

public record SuperAdminDashboardResponse(
    long totalLabs,
    long activeLabs,
    long suspendedLabs,
    long totalBranches,
    long activeBranches,
    long totalReportsGenerated,
    long activeUsers,
    long dailyUsage) {
}
