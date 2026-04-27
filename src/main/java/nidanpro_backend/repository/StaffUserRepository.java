package nidanpro_backend.repository;

import java.util.Optional;
import nidanpro_backend.model.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {
  Optional<StaffUser> findByEmailIgnoreCase(String email);
}
