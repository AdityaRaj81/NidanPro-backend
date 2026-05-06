package nidanpro_backend.dto;

public record CreateBranchRequest(
    Long labId,
    String branchName,
    String address,
    String phone,
    Boolean active) {
}
