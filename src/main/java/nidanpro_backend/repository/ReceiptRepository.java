package nidanpro_backend.repository;

import java.util.Optional;
import nidanpro_backend.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
  Optional<Receipt> findByReceiptNumberIgnoreCase(String receiptNumber);
}