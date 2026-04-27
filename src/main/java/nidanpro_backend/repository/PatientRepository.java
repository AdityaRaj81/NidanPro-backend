package nidanpro_backend.repository;

import java.util.List;
import java.util.Optional;
import nidanpro_backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
  Optional<Patient> findByPatientCodeIgnoreCase(String patientCode);

  List<Patient> findByPhoneNumber(String phoneNumber);
}
