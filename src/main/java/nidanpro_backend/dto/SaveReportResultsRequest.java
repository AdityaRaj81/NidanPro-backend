package nidanpro_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SaveReportResultsRequest(
    @NotEmpty List<ResultItem> results) {
  public record ResultItem(Long parameterId, String value) {
  }
}
