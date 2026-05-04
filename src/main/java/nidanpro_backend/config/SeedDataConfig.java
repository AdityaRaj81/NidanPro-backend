package nidanpro_backend.config;

import lombok.RequiredArgsConstructor;
import nidanpro_backend.model.Role;
import nidanpro_backend.model.StaffUser;
import nidanpro_backend.repository.RoleRepository;
import nidanpro_backend.repository.StaffUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Configuration
@RequiredArgsConstructor
public class SeedDataConfig {

  private final StaffUserRepository staffUserRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Bean
  CommandLineRunner seedSuperAdmin(
      @Value("${app.seed.super-admin.email}") String superAdminEmail,
      @Value("${app.seed.super-admin.password}") String superAdminPassword,
      @Value("${app.seed.super-admin.name}") String superAdminName) {
    return args -> {
      Role superAdminRole = ensureRole("SUPER_ADMIN");
      ensureRole("ADMIN");
      ensureRole("SAMPLE_COLLECTOR");
      ensureRole("TECHNICIAN");
      ensureRole("PATHOLOGIST");

      if (!StringUtils.hasText(superAdminEmail) || !StringUtils.hasText(superAdminPassword)) {
        return;
      }

      // Backfill missing employee codes for existing users
      for (StaffUser existingUser : staffUserRepository.findAll()) {
          if (!StringUtils.hasText(existingUser.getEmployeeCode())) {
              existingUser.setEmployeeCode("EMP" + java.time.Year.now().getValue() + java.util.UUID.randomUUID().toString().substring(0, 4).toUpperCase());
              staffUserRepository.save(existingUser);
          }
      }

      if (staffUserRepository.findByEmailIgnoreCase(superAdminEmail).isPresent()) {
        return;
      }

      StaffUser superAdmin = new StaffUser();
      superAdmin.setName(superAdminName);
      superAdmin.setEmployeeCode("ADMIN" + java.time.Year.now().getValue());
      superAdmin.setEmail(superAdminEmail.trim().toLowerCase());
      superAdmin.setPasswordHash(passwordEncoder.encode(superAdminPassword));
      superAdmin.setRole(superAdminRole);
      superAdmin.setActive(true);
      staffUserRepository.save(superAdmin);
    };
  }

  private Role ensureRole(String roleName) {
    return roleRepository.findByRoleNameIgnoreCase(roleName)
        .orElseGet(() -> {
          Role role = new Role();
          role.setRoleName(roleName);
          return roleRepository.save(role);
        });
  }
}
