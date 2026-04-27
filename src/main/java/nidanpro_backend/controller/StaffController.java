package nidanpro_backend.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.CreateStaffRequest;
import nidanpro_backend.dto.StaffResponse;
import nidanpro_backend.service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

  private final StaffService staffService;

  @PostMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<StaffResponse> create(@Valid @RequestBody CreateStaffRequest request) {
    return new ResponseEntity<>(staffService.create(request), HttpStatus.CREATED);
  }

  @GetMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<List<StaffResponse>> list() {
    return ResponseEntity.ok(staffService.list());
  }

  @PatchMapping("/{id}/active")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<StaffResponse> setActive(@PathVariable Long id, @RequestParam boolean active) {
    return ResponseEntity.ok(staffService.setActive(id, active));
  }
}
