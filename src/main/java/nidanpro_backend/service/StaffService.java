package nidanpro_backend.service;

import java.util.List;
import java.util.UUID;
import java.time.Year;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.CreateStaffRequest;
import nidanpro_backend.dto.StaffResponse;
import nidanpro_backend.model.Role;
import nidanpro_backend.model.StaffUser;
import nidanpro_backend.repository.RoleRepository;
import nidanpro_backend.repository.StaffUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffService {

  private final StaffUserRepository staffUserRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public StaffResponse create(CreateStaffRequest request) {
    if (request.email() != null && !request.email().isBlank() && staffUserRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
      throw new IllegalArgumentException("Email is already registered");
    }

    StaffUser user = new StaffUser();
    user.setName(request.fullName());
    user.setEmployeeCode(String.format("EMP%d%s", Year.now().getValue(), UUID.randomUUID().toString().substring(0, 4).toUpperCase()));
    user.setEmail(request.email() != null && !request.email().isBlank() ? request.email().trim().toLowerCase() : null);
    user.setPhone(request.phone());
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setRole(resolveRole(request.role()));
    user.setSignatureUrl(request.signatureUrl());
    user.setActive(request.active());

    StaffUser saved = staffUserRepository.save(user);
    return toDto(saved);
  }

  public List<StaffResponse> list() {
    return staffUserRepository.findAll().stream().map(this::toDto).toList();
  }

  public StaffResponse setActive(Long id, boolean active) {
    StaffUser user = staffUserRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Staff not found"));
    user.setActive(active);
    return toDto(staffUserRepository.save(user));
  }

  private StaffResponse toDto(StaffUser user) {
    return new StaffResponse(user.getId(), user.getName(), user.getEmployeeCode(), user.getEmail(), user.getRole().getRoleName(), user.isActive());
  }

  private Role resolveRole(String roleName) {
    String normalizedRole = roleName == null ? null : roleName.trim().toUpperCase();
    return roleRepository.findByRoleNameIgnoreCase(normalizedRole)
        .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + roleName));
  }
}
