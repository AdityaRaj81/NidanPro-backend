package nidanpro_backend.dto;

import java.time.Instant;

public record BranchSummaryResponse(
    Long id,
    Long labId,
    String labName,
    String branchName,
    String address,
    String phone,
    boolean active,
    Instant createdAt) {
}
