package nidanpro_backend.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.BranchSummaryResponse;
import nidanpro_backend.dto.CreateBranchRequest;
import nidanpro_backend.dto.CreateLabRequest;
import nidanpro_backend.dto.LabSummaryResponse;
import nidanpro_backend.dto.SuperAdminDashboardResponse;
import nidanpro_backend.model.Branch;
import nidanpro_backend.model.Lab;
import nidanpro_backend.model.Role;
import nidanpro_backend.model.StaffUser;
import nidanpro_backend.repository.BranchRepository;
import nidanpro_backend.repository.LabRepository;
import nidanpro_backend.repository.LabReportRepository;
import nidanpro_backend.repository.RoleRepository;
import nidanpro_backend.repository.StaffUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

  private final LabRepository labRepository;
  private final BranchRepository branchRepository;
  private final LabReportRepository labReportRepository;
  private final StaffUserRepository staffUserRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public SuperAdminDashboardResponse dashboard() {
    long totalLabs = labRepository.count();
    long activeLabs = labRepository.countByActiveTrue();
    long suspendedLabs = labRepository.countByActiveFalse();
    long totalBranches = branchRepository.count();
    long activeBranches = branchRepository.countByActiveTrue();
    long totalReports = labReportRepository.count();
    long activeUsers = staffUserRepository.countByActiveTrue();

    Instant dayStart = LocalDate.now(ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC);
    long dailyUsage = labReportRepository.findAll().stream()
        .filter(report -> report.getCreatedAt() != null && report.getCreatedAt().isAfter(dayStart))
        .count();

    return new SuperAdminDashboardResponse(
        totalLabs,
        activeLabs,
        suspendedLabs,
        totalBranches,
        activeBranches,
        totalReports,
        activeUsers,
        dailyUsage);
  }

  public List<LabSummaryResponse> listLabs() {
    return labRepository.findAll().stream()
        .map(lab -> new LabSummaryResponse(
            lab.getId(),
            lab.getLabName(),
            lab.getSubdomain(),
            lab.getCustomDomain(),
            lab.getSubscriptionPlan(),
            lab.getSubscriptionExpiry(),
            lab.getPaymentStatus(),
            lab.isActive(),
            lab.getCreatedAt(),
            branchRepository.countByLab_Id(lab.getId())))
        .toList();
  }

  public List<BranchSummaryResponse> listBranches() {
    return branchRepository.findAll().stream()
        .map(branch -> new BranchSummaryResponse(
            branch.getId(),
            branch.getLab() != null ? branch.getLab().getId() : null,
            branch.getLab() != null ? branch.getLab().getLabName() : null,
            branch.getBranchName(),
            branch.getAddress(),
            branch.getPhone(),
            branch.isActive(),
            branch.getCreatedAt()))
        .toList();
  }

  public Lab createLab(CreateLabRequest request) {
    if (request.labName() == null || request.labName().isBlank()) {
      throw new IllegalArgumentException("Lab name is required");
    }

    if (request.subdomain() != null && !request.subdomain().isBlank()
        && labRepository.findBySubdomainIgnoreCase(request.subdomain()).isPresent()) {
      throw new IllegalArgumentException("Subdomain already exists");
    }

    if (request.customDomain() != null && !request.customDomain().isBlank()
        && labRepository.findByCustomDomainIgnoreCase(request.customDomain()).isPresent()) {
      throw new IllegalArgumentException("Custom domain already exists");
    }

    Lab lab = new Lab();
    lab.setLabName(request.labName().trim());
    lab.setSubdomain(trimToNull(request.subdomain()));
    lab.setCustomDomain(trimToNull(request.customDomain()));
    lab.setLogoUrl(trimToNull(request.logoUrl()));
    lab.setPrimaryColor(trimToNull(request.primaryColor()));
    lab.setSecondaryColor(trimToNull(request.secondaryColor()));
    lab.setSubscriptionPlan(defaultString(request.subscriptionPlan(), "BASIC"));
    lab.setSubscriptionExpiry(request.subscriptionExpiry());
    lab.setPaymentStatus(defaultString(request.paymentStatus(), "PENDING"));
    lab.setActive(request.active() == null || request.active());
    Lab savedLab = labRepository.save(lab);

    if (request.adminEmail() != null && !request.adminEmail().isBlank()) {
      createLabOwner(request, savedLab);
    }

    return savedLab;
  }

  public Branch createBranch(CreateBranchRequest request) {
    Lab lab = labRepository.findById(request.labId())
        .orElseThrow(() -> new IllegalArgumentException("Lab not found"));

    Branch branch = new Branch();
    branch.setLab(lab);
    branch.setBranchName(request.branchName().trim());
    branch.setAddress(trimToNull(request.address()));
    branch.setPhone(trimToNull(request.phone()));
    branch.setActive(request.active() == null || request.active());
    return branchRepository.save(branch);
  }

  private String trimToNull(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private String defaultString(String value, String fallback) {
    String trimmed = trimToNull(value);
    return trimmed == null ? fallback : trimmed;
  }

  private void createLabOwner(CreateLabRequest request, Lab lab) {
    if (request.adminName() == null || request.adminName().isBlank()) {
      throw new IllegalArgumentException("Admin name is required for lab owner account");
    }
    if (request.adminPassword() == null || request.adminPassword().isBlank()) {
      throw new IllegalArgumentException("Admin password is required for lab owner account");
    }

    String adminEmail = request.adminEmail().trim().toLowerCase();
    if (staffUserRepository.findByEmailIgnoreCase(adminEmail).isPresent()) {
      throw new IllegalArgumentException("Admin email already exists");
    }

    Role adminRole = roleRepository.findByRoleNameIgnoreCase("ADMIN")
        .orElseThrow(() -> new IllegalArgumentException("ADMIN role is not configured"));

    StaffUser owner = new StaffUser();
    owner.setName(request.adminName().trim());
    owner.setEmail(adminEmail);
    owner.setPhone(trimToNull(request.adminPhone()));
    owner.setPasswordHash(passwordEncoder.encode(request.adminPassword()));
    owner.setRole(adminRole);
    owner.setLab(lab);
    owner.setActive(true);
    owner.setEmployeeCode("LAB-" + lab.getId());
    staffUserRepository.save(owner);
  }
}
