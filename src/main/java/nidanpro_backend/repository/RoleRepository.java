package nidanpro_backend.repository;

import java.util.Optional;
import nidanpro_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByRoleNameIgnoreCase(String roleName);
}