package nidanpro_backend.repository;

import java.util.Optional;
import nidanpro_backend.model.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {
  Optional<StaffUser> findByEmailIgnoreCase(String email);

  Optional<StaffUser> findByEmployeeCode(String employeeCode);

  long countByActiveTrue();

  long countByLab_Id(Long labId);

  long countByBranch_Id(Long branchId);
}
