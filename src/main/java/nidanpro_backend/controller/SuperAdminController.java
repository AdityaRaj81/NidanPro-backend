package nidanpro_backend.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.BranchSummaryResponse;
import nidanpro_backend.dto.CreateBranchRequest;
import nidanpro_backend.dto.CreateLabRequest;
import nidanpro_backend.dto.LabSummaryResponse;
import nidanpro_backend.dto.SuperAdminDashboardResponse;
import nidanpro_backend.model.Branch;
import nidanpro_backend.model.Lab;
import nidanpro_backend.service.SuperAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
  public ResponseEntity<Lab> createLab(@Valid @RequestBody CreateLabRequest request) {
    return new ResponseEntity<>(superAdminService.createLab(request), HttpStatus.CREATED);
  }

  @PostMapping("/branches")
  public ResponseEntity<Branch> createBranch(@Valid @RequestBody CreateBranchRequest request) {
    return new ResponseEntity<>(superAdminService.createBranch(request), HttpStatus.CREATED);
  }
}
