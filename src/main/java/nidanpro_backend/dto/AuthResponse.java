package nidanpro_backend.dto;

public record AuthResponse(
        String token,
        String email,
        String fullName,
        String role,
        Long labId,
        String labName,
        Long branchId,
        String branchName) {
}
