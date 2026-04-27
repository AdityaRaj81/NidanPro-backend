package nidanpro_backend.dto;

public record VerifyReportRequest(
    boolean approve,
    String remarks) {
}
