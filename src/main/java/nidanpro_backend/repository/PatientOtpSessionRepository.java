package nidanpro_backend.repository;

import java.time.Instant;
import java.util.Optional;
import nidanpro_backend.model.PatientOtpSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientOtpSessionRepository extends JpaRepository<PatientOtpSession, Long> {
  Optional<PatientOtpSession> findTopByPhoneNumberAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(String phoneNumber,
      Instant now);
}
