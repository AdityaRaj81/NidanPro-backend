package nidanpro_backend.dto;

public record StaffResponse(
    Long id,
    String fullName,
    String employeeCode,
    String email,
    String role,
    boolean active) {
}
