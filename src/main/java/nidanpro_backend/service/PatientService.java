package nidanpro_backend.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.CreatePatientRequest;
import nidanpro_backend.model.StaffUser;
import nidanpro_backend.model.Patient;
import nidanpro_backend.repository.StaffUserRepository;
import nidanpro_backend.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

  private final PatientRepository patientRepository;
  private final StaffUserRepository staffUserRepository;

  public Patient createPatient(CreatePatientRequest request, String requesterUsername) {
    validateRequest(request);

    AgeBreakdown ageBreakdown = resolveAge(request);
    StaffUser requester = resolveRequester(requesterUsername);

    Patient patient = new Patient();
    patient.setPatientCode(generatePatientCode());
    patient.setFullName(request.fullName());
    patient.setPhoneNumber(request.phoneNumber().replaceAll("\\D", ""));
    patient.setDateOfBirth(request.dateOfBirth());
    patient.setAge(ageBreakdown.years());
    patient.setAgeMonths(ageBreakdown.months());
    patient.setAgeDays(ageBreakdown.days());
    patient.setGender(request.gender());
    patient.setAddress(request.address());
    if (requester != null) {
      patient.setLab(requester.getLab());
    }
    return patientRepository.save(patient);
  }

  public List<Patient> listPatients() {
    return patientRepository.findAll();
  }

  public List<Patient> findByPhone(String phone) {
    return patientRepository.findByPhoneNumber(phone.replaceAll("\\D", ""));
  }

  public Optional<Patient> findByCode(String patientCode) {
    return patientRepository.findByPatientCodeIgnoreCase(patientCode);
  }

  private void validateRequest(CreatePatientRequest request) {
    if (request.gender() == null) {
      throw new IllegalArgumentException("Gender is required");
    }

    boolean hasDob = request.dateOfBirth() != null;
    boolean hasAge = request.ageYears() != null;

    if (!hasDob && !hasAge) {
      throw new IllegalArgumentException("Provide either dateOfBirth or ageYears");
    }

    if (hasDob && request.dateOfBirth().isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date of birth cannot be in the future");
    }

    if (hasAge && request.ageYears() < 0) {
      throw new IllegalArgumentException("ageYears must be zero or positive");
    }

    if (request.ageMonths() != null && (request.ageMonths() < 0 || request.ageMonths() > 11)) {
      throw new IllegalArgumentException("ageMonths must be between 0 and 11");
    }

    if (request.ageDays() != null && (request.ageDays() < 0 || request.ageDays() > 31)) {
      throw new IllegalArgumentException("ageDays must be between 0 and 31");
    }
  }

  private AgeBreakdown resolveAge(CreatePatientRequest request) {
    if (request.dateOfBirth() != null) {
      Period period = Period.between(request.dateOfBirth(), LocalDate.now());
      return new AgeBreakdown(period.getYears(), period.getMonths(), period.getDays());
    }

    return new AgeBreakdown(
        request.ageYears(),
        request.ageMonths() == null ? 0 : request.ageMonths(),
        request.ageDays() == null ? 0 : request.ageDays());
  }

  private String generatePatientCode() {
    long count = patientRepository.count() + 1;
    return "P" + String.format("%04d", count);
  }

  private StaffUser resolveRequester(String requesterUsername) {
    if (requesterUsername == null || requesterUsername.isBlank()) {
      return null;
    }
    if (requesterUsername.contains("@")) {
      return staffUserRepository.findByEmailIgnoreCase(requesterUsername).orElse(null);
    }
    return staffUserRepository.findByEmployeeCode(requesterUsername.toUpperCase()).orElse(null);
  }

  private record AgeBreakdown(int years, int months, int days) {
  }
}
