package nidanpro_backend.dto;

public record LabSettingsRequest(
    String labName,
    String address,
    String phone,
    String email,
    String website,
    String logoUrl,
    String signatureUrl) {
}
