package nidanpro_backend.repository;

import java.util.List;
import java.util.Optional;
import nidanpro_backend.model.LabReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabReportRepository extends JpaRepository<LabReport, Long> {
  Optional<LabReport> findByReportCodeIgnoreCase(String reportCode);

  List<LabReport> findByPatientId(Long patientId);
}
