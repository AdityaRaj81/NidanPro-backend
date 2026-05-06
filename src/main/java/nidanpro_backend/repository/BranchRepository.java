package nidanpro_backend.repository;

import java.util.List;
import nidanpro_backend.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
  long countByActiveTrue();

  long countByActiveFalse();

  long countByLab_Id(Long labId);

  List<Branch> findByLab_IdOrderByCreatedAtDesc(Long labId);
}
