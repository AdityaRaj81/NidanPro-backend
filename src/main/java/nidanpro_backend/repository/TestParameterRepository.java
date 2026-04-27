package nidanpro_backend.repository;

import java.util.List;
import nidanpro_backend.model.TestParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestParameterRepository extends JpaRepository<TestParameter, Long> {
  List<TestParameter> findByLabTest_Id(Long testId);
}
