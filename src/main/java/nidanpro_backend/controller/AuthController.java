package nidanpro_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.AuthResponse;
import nidanpro_backend.dto.StaffLoginRequest;
import nidanpro_backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody StaffLoginRequest request) {
    return ResponseEntity.ok(authService.loginStaff(request.email(), request.password()));
  }
}
