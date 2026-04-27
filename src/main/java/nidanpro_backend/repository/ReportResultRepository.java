package nidanpro_backend.repository;

import java.util.List;
import nidanpro_backend.model.ReportResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportResultRepository extends JpaRepository<ReportResult, Long> {
  List<ReportResult> findByReportId(Long reportId);
}
