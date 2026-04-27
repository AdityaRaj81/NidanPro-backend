package nidanpro_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateTestRequest(
    @NotBlank String testCode,
    @NotBlank String testName,
    String description,
    @NotNull BigDecimal price,
    boolean active) {
}
