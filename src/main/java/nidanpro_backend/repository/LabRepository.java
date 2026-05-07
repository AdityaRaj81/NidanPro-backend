package nidanpro_backend.repository;

import java.util.Optional;
import nidanpro_backend.model.Lab;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabRepository extends JpaRepository<Lab, Long> {
  long countByActiveTrue();

  long countByActiveFalse();

  Optional<Lab> findBySubdomainIgnoreCase(String subdomain);

  Optional<Lab> findByLabNumberIgnoreCase(String labNumber);

  Optional<Lab> findByCustomDomainIgnoreCase(String customDomain);
}
