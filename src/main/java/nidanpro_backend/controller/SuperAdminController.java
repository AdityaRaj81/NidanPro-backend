package nidanpro_backend.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.BranchSummaryResponse;
import nidanpro_backend.dto.CreateBranchRequest;
import nidanpro_backend.dto.CreateLabRequest;
import nidanpro_backend.dto.CreateLabPaymentRequest;
import nidanpro_backend.dto.LabSummaryResponse;
import nidanpro_backend.dto.LabPaymentHistoryResponse;
import nidanpro_backend.dto.SuperAdminSettingsResponse;
import nidanpro_backend.dto.SuperAdminDashboardResponse;
import nidanpro_backend.model.Branch;
import nidanpro_backend.service.SuperAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

  private final SuperAdminService superAdminService;

  @GetMapping("/dashboard")
  public ResponseEntity<SuperAdminDashboardResponse> dashboard() {
    return ResponseEntity.ok(superAdminService.dashboard());
  }

  @GetMapping("/labs")
  public ResponseEntity<List<LabSummaryResponse>> listLabs() {
    return ResponseEntity.ok(superAdminService.listLabs());
  }

  @GetMapping("/branches")
  public ResponseEntity<List<BranchSummaryResponse>> listBranches() {
    return ResponseEntity.ok(superAdminService.listBranches());
  }

  @PostMapping("/labs")
  public ResponseEntity<LabSummaryResponse> createLab(@Valid @RequestBody CreateLabRequest request) {
    var created = superAdminService.createLab(request);
    return new ResponseEntity<>(superAdminService.getLab(created.getId()), HttpStatus.CREATED);
  }

  @GetMapping("/labs/{id}")
  public ResponseEntity<LabSummaryResponse> getLab(@PathVariable Long id) {
    return ResponseEntity.ok(superAdminService.getLab(id));
  }

  @PutMapping("/labs/{id}")
  public ResponseEntity<LabSummaryResponse> updateLab(
      @PathVariable Long id,
      @Valid @RequestBody CreateLabRequest request) {
    return ResponseEntity.ok(superAdminService.updateLab(id, request));
  }

  @DeleteMapping("/labs/{id}")
  public ResponseEntity<Void> deleteLab(@PathVariable Long id) {
    superAdminService.deleteLab(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/labs/{id}/payments")
  public ResponseEntity<List<LabPaymentHistoryResponse>> listLabPayments(@PathVariable Long id) {
    return ResponseEntity.ok(superAdminService.listLabPayments(id));
  }

  @PostMapping("/labs/{id}/payments")
  public ResponseEntity<LabPaymentHistoryResponse> createLabPayment(
      @PathVariable Long id,
      @RequestBody CreateLabPaymentRequest request) {
    return new ResponseEntity<>(superAdminService.createLabPayment(id, request), HttpStatus.CREATED);
  }

  @GetMapping("/settings")
  public ResponseEntity<SuperAdminSettingsResponse> settings() {
    return ResponseEntity.ok(superAdminService.settingsSummary());
  }

  @PostMapping("/branches")
  public ResponseEntity<Branch> createBranch(@Valid @RequestBody CreateBranchRequest request) {
    return new ResponseEntity<>(superAdminService.createBranch(request), HttpStatus.CREATED);
  }

  @GetMapping("/branches/{id}")
  public ResponseEntity<BranchSummaryResponse> getBranch(@PathVariable Long id) {
    return ResponseEntity.ok(superAdminService.getBranch(id));
  }

  @PutMapping("/branches/{id}")
  public ResponseEntity<BranchSummaryResponse> updateBranch(
      @PathVariable Long id,
      @Valid @RequestBody CreateBranchRequest request) {
    return ResponseEntity.ok(superAdminService.updateBranch(id, request));
  }

  @DeleteMapping("/branches/{id}")
  public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
    superAdminService.deleteBranch(id);
    return ResponseEntity.noContent().build();
  }
}
