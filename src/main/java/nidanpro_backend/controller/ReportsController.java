package nidanpro_backend.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.CreateReportRequest;
import nidanpro_backend.dto.ReportSummaryResponse;
import nidanpro_backend.dto.SaveReportResultsRequest;
import nidanpro_backend.dto.VerifyReportRequest;
import nidanpro_backend.model.LabReport;
import nidanpro_backend.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportsController {

  private final ReportService reportService;

  @PostMapping("/api/reports")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','SAMPLE_COLLECTOR')")
  public ResponseEntity<LabReport> createReport(
      @Valid @RequestBody CreateReportRequest request,
      Principal principal) {
    return new ResponseEntity<>(reportService.createReport(request, principal.getName()), HttpStatus.CREATED);
  }

  @GetMapping("/api/reports")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SAMPLE_COLLECTOR','TECHNICIAN','PATHOLOGIST')")
  public ResponseEntity<List<ReportSummaryResponse>> listReports() {
    return ResponseEntity.ok(reportService.listReports());
  }

  @PutMapping("/api/reports/{id}/results")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','TECHNICIAN')")
  public ResponseEntity<LabReport> saveResults(
      @PathVariable Long id,
      @Valid @RequestBody SaveReportResultsRequest request,
      Principal principal) {
    return ResponseEntity.ok(reportService.saveResults(id, request, principal.getName()));
  }

  @PutMapping("/api/reports/{id}/verify")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','PATHOLOGIST')")
  public ResponseEntity<LabReport> verify(
      @PathVariable Long id,
      @RequestBody VerifyReportRequest request,
      Principal principal) {
    return ResponseEntity.ok(reportService.verifyReport(id, request, principal.getName()));
  }

  @GetMapping("/api/patient-reports/{reportCode}")
  @PreAuthorize("permitAll()")
  public ResponseEntity<LabReport> getByCode(@PathVariable String reportCode) {
    return ResponseEntity.ok(reportService.getByCode(reportCode));
  }

  @GetMapping("/api/patient-reports")
  @PreAuthorize("permitAll()")
  public ResponseEntity<List<ReportSummaryResponse>> listByPatient(@RequestParam Long patientId) {
    return ResponseEntity.ok(reportService.listPatientReports(patientId));
  }
}
