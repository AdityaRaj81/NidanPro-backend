package nidanpro_backend.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.CreateReportRequest;
import nidanpro_backend.dto.ReportSummaryResponse;
import nidanpro_backend.dto.SaveReportResultsRequest;
import nidanpro_backend.dto.VerifyReportRequest;
import nidanpro_backend.model.LabReport;
import nidanpro_backend.model.ReportResult;
import nidanpro_backend.model.ReportStatus;
import nidanpro_backend.model.ReportStatusHistory;
import nidanpro_backend.repository.LabReportRepository;
import nidanpro_backend.repository.LabTestRepository;
import nidanpro_backend.repository.PatientRepository;
import nidanpro_backend.repository.ReportResultRepository;
import nidanpro_backend.repository.ReportStatusHistoryRepository;
import nidanpro_backend.repository.StaffUserRepository;
import nidanpro_backend.repository.TestParameterRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final LabReportRepository labReportRepository;
  private final PatientRepository patientRepository;
  private final LabTestRepository labTestRepository;
  private final StaffUserRepository staffUserRepository;
  private final TestParameterRepository testParameterRepository;
  private final ReportResultRepository reportResultRepository;
  private final ReportStatusHistoryRepository reportStatusHistoryRepository;

  public LabReport createReport(CreateReportRequest request, String staffEmail) {
    LabReport report = new LabReport();
    report.setReportCode(generateReportCode());
    report.setPatient(patientRepository.findById(request.patientId())
        .orElseThrow(() -> new IllegalArgumentException("Patient not found")));
    report.setLabTest(labTestRepository.findById(request.testId())
        .orElseThrow(() -> new IllegalArgumentException("Test not found")));
    report.setStatus(ReportStatus.SAMPLE_PENDING);
    report.setEnteredBy(staffUserRepository.findByEmailIgnoreCase(staffEmail)
        .orElseThrow(() -> new IllegalArgumentException("Staff not found")));

    LabReport saved = labReportRepository.save(report);
    recordStatus(saved, ReportStatus.SAMPLE_PENDING, saved.getEnteredBy(), "Report created");
    return saved;
  }

  public List<ReportSummaryResponse> listReports() {
    return labReportRepository.findAll().stream()
        .map(r -> new ReportSummaryResponse(
            r.getId(),
            r.getReportCode(),
            r.getPatient().getFullName(),
            r.getLabTest().getTestName(),
            r.getStatus(),
            r.getCreatedAt()))
        .toList();
  }

  public List<ReportSummaryResponse> listPatientReports(Long patientId) {
    return labReportRepository.findByPatientId(patientId).stream()
        .map(r -> new ReportSummaryResponse(
            r.getId(),
            r.getReportCode(),
            r.getPatient().getFullName(),
            r.getLabTest().getTestName(),
            r.getStatus(),
            r.getCreatedAt()))
        .toList();
  }

  public LabReport getByCode(String reportCode) {
    return labReportRepository.findByReportCodeIgnoreCase(reportCode)
        .orElseThrow(() -> new IllegalArgumentException("Report not found"));
  }

  public LabReport saveResults(Long reportId, SaveReportResultsRequest request, String staffEmail) {
    LabReport report = labReportRepository.findById(reportId)
        .orElseThrow(() -> new IllegalArgumentException("Report not found"));

    List<ReportResult> existing = reportResultRepository.findByReportId(reportId);
    if (!existing.isEmpty()) {
      reportResultRepository.deleteAll(existing);
    }

    List<ReportResult> newResults = new ArrayList<>();
    for (var item : request.results()) {
      if (item.parameterId() == null || item.value() == null || item.value().isBlank()) {
        continue;
      }
      ReportResult result = new ReportResult();
      result.setReport(report);
      result.setParameter(testParameterRepository.findById(item.parameterId())
          .orElseThrow(() -> new IllegalArgumentException("Parameter not found")));
      result.setValue(item.value());
      newResults.add(result);
    }
    reportResultRepository.saveAll(newResults);

    report.setStatus(ReportStatus.COMPLETED);
    LabReport saved = labReportRepository.save(report);
    recordStatus(saved, ReportStatus.COMPLETED,
      staffUserRepository.findByEmailIgnoreCase(staffEmail).orElse(saved.getEnteredBy()),
      "Results saved");
    return saved;
  }

  public LabReport verifyReport(Long reportId, VerifyReportRequest request, String staffEmail) {
    LabReport report = labReportRepository.findById(reportId)
        .orElseThrow(() -> new IllegalArgumentException("Report not found"));

    report.setRemarks(request.remarks());
    report.setVerifiedBy(staffUserRepository.findByEmailIgnoreCase(staffEmail)
        .orElseThrow(() -> new IllegalArgumentException("Staff not found")));

    if (request.approve()) {
      report.setStatus(ReportStatus.VERIFIED);
      report.setVerifiedAt(Instant.now());
    } else {
      report.setStatus(ReportStatus.REJECTED);
    }

    LabReport saved = labReportRepository.save(report);
    recordStatus(saved, saved.getStatus(), saved.getVerifiedBy(), request.remarks());
    return saved;
  }

  private String generateReportCode() {
    long count = labReportRepository.count() + 1;
    return "RPT" + String.format("%06d", count);
  }

  private void recordStatus(LabReport report, ReportStatus status, nidanpro_backend.model.StaffUser changedBy, String remarks) {
    ReportStatusHistory history = new ReportStatusHistory();
    history.setReport(report);
    history.setStatus(status);
    history.setChangedBy(changedBy);
    history.setRemarks(remarks);
    reportStatusHistoryRepository.save(history);
  }
}
