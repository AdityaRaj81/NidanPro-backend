package nidanpro_backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import nidanpro_backend.model.RangeRuleType;

public record CreateTestParameterRequest(
        @NotBlank String parameterName,
        @NotBlank String unit,
        RangeRuleType rangeRuleType,
        BigDecimal lowerBound,
        BigDecimal upperBound,
        String referenceRange) {
}
