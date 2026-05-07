package nidanpro_backend.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import nidanpro_backend.dto.CreateLabPaymentRequest;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.BranchSummaryResponse;
import nidanpro_backend.dto.CreateBranchRequest;
import nidanpro_backend.dto.CreateLabRequest;
import nidanpro_backend.dto.LabSummaryResponse;
import nidanpro_backend.dto.LabPaymentHistoryResponse;
import nidanpro_backend.dto.SuperAdminSettingsResponse;
import nidanpro_backend.dto.SuperAdminDashboardResponse;
import nidanpro_backend.model.Branch;
import nidanpro_backend.model.Lab;
import nidanpro_backend.model.LabPaymentHistory;
import nidanpro_backend.model.Role;
import nidanpro_backend.model.StaffUser;
import nidanpro_backend.repository.BranchRepository;
import nidanpro_backend.repository.LabPaymentHistoryRepository;
import nidanpro_backend.repository.LabRepository;
import nidanpro_backend.repository.LabReportRepository;
import nidanpro_backend.repository.RoleRepository;
import nidanpro_backend.repository.StaffUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

  private final LabRepository labRepository;
  private final BranchRepository branchRepository;
  private final LabPaymentHistoryRepository labPaymentHistoryRepository;
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
            lab.getLabNumber(),
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
    validateLabRequest(request, null);

    Lab lab = new Lab();
    applyLabRequest(lab, request);
    Lab savedLab = labRepository.save(lab);

    if (request.adminEmail() != null && !request.adminEmail().isBlank()) {
      createLabOwner(request, savedLab);
    }

    return savedLab;
  }

  public LabSummaryResponse getLab(Long id) {
    Lab lab = findLab(id);
    return toLabSummary(lab);
  }

  public LabSummaryResponse updateLab(Long id, CreateLabRequest request) {
    Lab existingLab = findLab(id);
    validateLabRequest(request, id);
    applyLabRequest(existingLab, request);
    Lab saved = labRepository.save(existingLab);
    return toLabSummary(saved);
  }

  public void deleteLab(Long id) {
    Lab lab = findLab(id);
    long branchCount = branchRepository.countByLab_Id(id);
    if (branchCount > 0) {
      throw new IllegalArgumentException("Delete branches first before deleting this lab");
    }

    long staffCount = staffUserRepository.countByLab_Id(id);
    if (staffCount > 0) {
      throw new IllegalArgumentException("Cannot delete lab with assigned staff accounts");
    }

    labRepository.delete(lab);
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

  public BranchSummaryResponse getBranch(Long id) {
    Branch branch = branchRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));
    return new BranchSummaryResponse(
        branch.getId(),
        branch.getLab() != null ? branch.getLab().getId() : null,
        branch.getLab() != null ? branch.getLab().getLabName() : null,
        branch.getBranchName(),
        branch.getAddress(),
        branch.getPhone(),
        branch.isActive(),
        branch.getCreatedAt());
  }

  public BranchSummaryResponse updateBranch(Long id, CreateBranchRequest request) {
    Branch existing = branchRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

    Lab lab = labRepository.findById(request.labId())
        .orElseThrow(() -> new IllegalArgumentException("Lab not found"));

    existing.setLab(lab);
    existing.setBranchName(request.branchName().trim());
    existing.setAddress(trimToNull(request.address()));
    existing.setPhone(trimToNull(request.phone()));
    existing.setActive(request.active() == null || request.active());

    Branch saved = branchRepository.save(existing);
    return new BranchSummaryResponse(
        saved.getId(),
        saved.getLab() != null ? saved.getLab().getId() : null,
        saved.getLab() != null ? saved.getLab().getLabName() : null,
        saved.getBranchName(),
        saved.getAddress(),
        saved.getPhone(),
        saved.isActive(),
        saved.getCreatedAt());
  }

  public void deleteBranch(Long id) {
    Branch branch = branchRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

    long staffCount = staffUserRepository.countByBranch_Id(id);
    if (staffCount > 0) {
      throw new IllegalArgumentException("Cannot delete branch with assigned staff users");
    }

    branchRepository.delete(branch);
  }

  public List<LabPaymentHistoryResponse> listLabPayments(Long labId) {
    Lab lab = findLab(labId);
    return labPaymentHistoryRepository.findByLab_IdOrderByCreatedAtDesc(lab.getId()).stream()
        .map(payment -> new LabPaymentHistoryResponse(
            payment.getId(),
            lab.getId(),
            lab.getLabName(),
            payment.getSubscriptionPlan(),
            payment.getAmount(),
            payment.getPaymentStatus(),
            payment.getPeriodStart(),
            payment.getPeriodEnd(),
            payment.getPaidAt(),
            payment.getRemarks(),
            payment.getCreatedAt()))
        .toList();
  }

  public LabPaymentHistoryResponse createLabPayment(Long labId, CreateLabPaymentRequest request) {
    Lab lab = findLab(labId);

    if (request.amount() == null || request.amount().doubleValue() <= 0) {
      throw new IllegalArgumentException("Payment amount must be greater than zero");
    }

    LabPaymentHistory payment = new LabPaymentHistory();
    payment.setLab(lab);
    payment.setSubscriptionPlan(defaultString(request.subscriptionPlan(), lab.getSubscriptionPlan()));
    payment.setAmount(request.amount());
    payment.setPaymentStatus(defaultString(request.paymentStatus(), "PAID"));
    payment.setPeriodStart(request.periodStart());
    payment.setPeriodEnd(request.periodEnd());
    payment.setRemarks(trimToNull(request.remarks()));
    if ("PAID".equalsIgnoreCase(payment.getPaymentStatus())) {
      payment.setPaidAt(Instant.now());
    }

    LabPaymentHistory saved = labPaymentHistoryRepository.save(payment);

    lab.setSubscriptionPlan(saved.getSubscriptionPlan());
    if (saved.getPeriodEnd() != null) {
      lab.setSubscriptionExpiry(saved.getPeriodEnd());
    }
    lab.setPaymentStatus(saved.getPaymentStatus().toUpperCase());
    labRepository.save(lab);

    return new LabPaymentHistoryResponse(
        saved.getId(),
        lab.getId(),
        lab.getLabName(),
        saved.getSubscriptionPlan(),
        saved.getAmount(),
        saved.getPaymentStatus(),
        saved.getPeriodStart(),
        saved.getPeriodEnd(),
        saved.getPaidAt(),
        saved.getRemarks(),
        saved.getCreatedAt());
  }

  public SuperAdminSettingsResponse settingsSummary() {
    List<Lab> labs = labRepository.findAll();
    long totalLabs = labs.size();
    long activeLabs = labs.stream().filter(Lab::isActive).count();
    long suspendedLabs = totalLabs - activeLabs;

    long basicPlans = labs.stream().filter(l -> "BASIC".equalsIgnoreCase(l.getSubscriptionPlan())).count();
    long standardPlans = labs.stream().filter(l -> "STANDARD".equalsIgnoreCase(l.getSubscriptionPlan())).count();
    long proPlans = labs.stream().filter(l -> "PRO".equalsIgnoreCase(l.getSubscriptionPlan())).count();
    long enterprisePlans = labs.stream().filter(l -> "ENTERPRISE".equalsIgnoreCase(l.getSubscriptionPlan())).count();

    long paidLabs = labs.stream().filter(l -> "PAID".equalsIgnoreCase(l.getPaymentStatus())).count();
    long pendingLabs = labs.stream().filter(l -> "PENDING".equalsIgnoreCase(l.getPaymentStatus())).count();
    long overdueLabs = labs.stream().filter(l -> "OVERDUE".equalsIgnoreCase(l.getPaymentStatus())).count();
    long suspendedPayments = labs.stream().filter(l -> "SUSPENDED".equalsIgnoreCase(l.getPaymentStatus())).count();

    return new SuperAdminSettingsResponse(
        totalLabs,
        activeLabs,
        suspendedLabs,
        branchRepository.count(),
        staffUserRepository.count(),
        basicPlans,
        standardPlans,
        proPlans,
        enterprisePlans,
        paidLabs,
        pendingLabs,
        overdueLabs,
        suspendedPayments);
  }

  private void applyLabRequest(Lab lab, CreateLabRequest request) {
    lab.setLabName(request.labName().trim());
    lab.setLabNumber(trimToNull(request.labNumber()));
    lab.setSubdomain(trimToNull(request.subdomain()));
    lab.setCustomDomain(trimToNull(request.customDomain()));
    lab.setLogoUrl(trimToNull(request.logoUrl()));
    lab.setPrimaryColor(trimToNull(request.primaryColor()));
    lab.setSecondaryColor(trimToNull(request.secondaryColor()));
    lab.setSubscriptionPlan(defaultString(request.subscriptionPlan(), "BASIC").toUpperCase());
    lab.setSubscriptionExpiry(request.subscriptionExpiry());
    lab.setPaymentStatus(defaultString(request.paymentStatus(), "PENDING").toUpperCase());
    lab.setActive(request.active() == null || request.active());
  }

  private void validateLabRequest(CreateLabRequest request, Long currentLabId) {
    if (request.labName() == null || request.labName().isBlank()) {
      throw new IllegalArgumentException("Lab name is required");
    }
    if (request.labNumber() == null || request.labNumber().isBlank()) {
      throw new IllegalArgumentException("Lab number is required");
    }

    String labNumber = request.labNumber().trim();
    labRepository.findByLabNumberIgnoreCase(labNumber)
        .filter(existing -> !existing.getId().equals(currentLabId))
        .ifPresent(existing -> {
          throw new IllegalArgumentException("Lab number already exists");
        });

    String subdomain = trimToNull(request.subdomain());
    if (subdomain != null) {
      labRepository.findBySubdomainIgnoreCase(subdomain)
          .filter(existing -> !existing.getId().equals(currentLabId))
          .ifPresent(existing -> {
            throw new IllegalArgumentException("Subdomain already exists");
          });
    }

    String customDomain = trimToNull(request.customDomain());
    if (customDomain != null) {
      labRepository.findByCustomDomainIgnoreCase(customDomain)
          .filter(existing -> !existing.getId().equals(currentLabId))
          .ifPresent(existing -> {
            throw new IllegalArgumentException("Custom domain already exists");
          });
    }
  }

  private LabSummaryResponse toLabSummary(Lab lab) {
    return new LabSummaryResponse(
        lab.getId(),
        lab.getLabName(),
        lab.getLabNumber(),
        lab.getSubdomain(),
        lab.getCustomDomain(),
        lab.getSubscriptionPlan(),
        lab.getSubscriptionExpiry(),
        lab.getPaymentStatus(),
        lab.isActive(),
        lab.getCreatedAt(),
        branchRepository.countByLab_Id(lab.getId()));
  }

  private Lab findLab(Long id) {
    return labRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab not found"));
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
