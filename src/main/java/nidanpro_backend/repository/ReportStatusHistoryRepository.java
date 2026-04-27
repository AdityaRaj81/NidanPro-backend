package nidanpro_backend.repository;

import nidanpro_backend.model.ReportStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportStatusHistoryRepository extends JpaRepository<ReportStatusHistory, Long> {
}