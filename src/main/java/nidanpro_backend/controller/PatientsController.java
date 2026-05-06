package nidanpro_backend.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.CreatePatientRequest;
import nidanpro_backend.model.Patient;
import nidanpro_backend.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientsController {

  private final PatientService patientService;

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','SAMPLE_COLLECTOR')")
  public ResponseEntity<Patient> create(@Valid @RequestBody CreatePatientRequest request,
      java.security.Principal principal) {
    return new ResponseEntity<>(patientService.createPatient(request, principal.getName()), HttpStatus.CREATED);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','SAMPLE_COLLECTOR','TECHNICIAN','PATHOLOGIST')")
  public ResponseEntity<List<Patient>> list() {
    return ResponseEntity.ok(patientService.listPatients());
  }

  @GetMapping("/search")
  @PreAuthorize("hasAnyRole('ADMIN','SAMPLE_COLLECTOR','TECHNICIAN','PATHOLOGIST')")
  public ResponseEntity<List<Patient>> searchByPhone(@RequestParam String phone) {
    return ResponseEntity.ok(patientService.findByPhone(phone));
  }

  @GetMapping("/by-code")
  @PreAuthorize("hasAnyRole('ADMIN','SAMPLE_COLLECTOR','TECHNICIAN','PATHOLOGIST')")
  public ResponseEntity<Patient> searchByCode(@RequestParam String code) {
    return patientService.findByCode(code)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
