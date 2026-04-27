package nidanpro_backend.service;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.GenerateReceiptRequest;
import nidanpro_backend.dto.ReceiptResponse;
import nidanpro_backend.model.Patient;
import nidanpro_backend.model.Receipt;
import nidanpro_backend.model.StaffUser;
import nidanpro_backend.repository.PatientRepository;
import nidanpro_backend.repository.LabTestRepository;
import nidanpro_backend.repository.ReceiptRepository;
import nidanpro_backend.repository.StaffUserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceiptService {

  private final LabTestRepository labTestRepository;
  private final PatientRepository patientRepository;
  private final StaffUserRepository staffUserRepository;
  private final ReceiptRepository receiptRepository;

  public ReceiptResponse generateReceipt(GenerateReceiptRequest request, String staffEmail) {
    List<String> testNames = request.testIds().stream()
        .map(id -> labTestRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid test ID: " + id))
            .getTestName())
        .toList();

    String receiptCode = "REC-" + System.currentTimeMillis();
    String patientRef = request.patientCode() != null && !request.patientCode().isBlank()
        ? request.patientCode()
        : request.patientPhone();

    Patient patient = resolvePatient(request);
    StaffUser createdBy = staffUserRepository.findByEmailIgnoreCase(staffEmail)
        .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

    Receipt receipt = new Receipt();
    receipt.setReceiptNumber(receiptCode);
    receipt.setPatient(patient);
    receipt.setTotalTests(testNames.size());
    receipt.setCreatedBy(createdBy);
    receiptRepository.save(receipt);

    return new ReceiptResponse(
        receiptCode,
        Instant.now(),
        patientRef,
        request.patientName(),
        testNames);
  }

  private Patient resolvePatient(GenerateReceiptRequest request) {
    if (request.patientCode() != null && !request.patientCode().isBlank()) {
      return patientRepository.findByPatientCodeIgnoreCase(request.patientCode())
          .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
    }

    if (request.patientPhone() != null && !request.patientPhone().isBlank()) {
      return patientRepository.findByPhoneNumber(request.patientPhone().replaceAll("\\D", ""))
          .stream()
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
    }

    throw new IllegalArgumentException("Patient reference is required");
  }
}
