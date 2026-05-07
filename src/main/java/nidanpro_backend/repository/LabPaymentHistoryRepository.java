package nidanpro_backend.repository;

import java.util.List;
import nidanpro_backend.model.LabPaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabPaymentHistoryRepository extends JpaRepository<LabPaymentHistory, Long> {
  List<LabPaymentHistory> findByLab_IdOrderByCreatedAtDesc(Long labId);
}
