package nidanpro_backend.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.CreatePatientRequest;
import nidanpro_backend.model.Patient;
import nidanpro_backend.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

  private final PatientRepository patientRepository;

  public Patient createPatient(CreatePatientRequest request) {
    Patient patient = new Patient();
    patient.setPatientCode(generatePatientCode());
    patient.setFullName(request.fullName());
    patient.setPhoneNumber(request.phoneNumber().replaceAll("\\D", ""));
    patient.setDateOfBirth(request.dateOfBirth());
    patient.setAge(calculateAge(request.dateOfBirth()));
    patient.setGender(request.gender());
    patient.setAddress(request.address());
    return patientRepository.save(patient);
  }

  public List<Patient> listPatients() {
    return patientRepository.findAll();
  }

  public List<Patient> findByPhone(String phone) {
    return patientRepository.findByPhoneNumber(phone.replaceAll("\\D", ""));
  }

  private int calculateAge(LocalDate dob) {
    return Period.between(dob, LocalDate.now()).getYears();
  }

  private String generatePatientCode() {
    long count = patientRepository.count() + 1;
    return "P" + String.format("%04d", count);
  }
}
