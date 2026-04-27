package nidanpro_backend.repository;

import java.util.Optional;
import nidanpro_backend.model.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabTestRepository extends JpaRepository<LabTest, Long> {
  Optional<LabTest> findByTestCodeIgnoreCase(String testCode);
}
