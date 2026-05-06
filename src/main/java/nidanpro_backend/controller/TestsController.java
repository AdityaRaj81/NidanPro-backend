package nidanpro_backend.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.CreateTestParameterRequest;
import nidanpro_backend.dto.CreateTestRequest;
import nidanpro_backend.model.LabTest;
import nidanpro_backend.model.TestParameter;
import nidanpro_backend.service.TestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestsController {

  private final TestService testService;

  @PostMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<LabTest> create(@Valid @RequestBody CreateTestRequest request, Principal principal) {
    return new ResponseEntity<>(testService.createTest(request, principal.getName()), HttpStatus.CREATED);
  }

  @GetMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<List<LabTest>> list() {
    return ResponseEntity.ok(testService.listTests());
  }

  @GetMapping("/active")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SAMPLE_COLLECTOR','TECHNICIAN','PATHOLOGIST')")
  public ResponseEntity<List<LabTest>> listActive() {
    return ResponseEntity.ok(testService.listActiveTests());
  }

  @PostMapping("/{id}/parameters")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<TestParameter> addParameter(
      @PathVariable Long id,
      @Valid @RequestBody CreateTestParameterRequest request) {
    return new ResponseEntity<>(testService.addParameter(id, request), HttpStatus.CREATED);
  }

  @GetMapping("/{id}/parameters")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<List<TestParameter>> listParameters(@PathVariable Long id) {
    return ResponseEntity.ok(testService.listParameters(id));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<LabTest> getById(@PathVariable Long id) {
    return testService.getTestById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<LabTest> update(
      @PathVariable Long id,
      @Valid @RequestBody CreateTestRequest request,
      Principal principal) {
    return ResponseEntity.ok(testService.updateTest(id, request, principal.getName()));
  }
}
