package nidanpro_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.SendOtpRequest;
import nidanpro_backend.dto.SendOtpResponse;
import nidanpro_backend.dto.VerifyOtpRequest;
import nidanpro_backend.dto.VerifyOtpResponse;
import nidanpro_backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patient-auth")
@RequiredArgsConstructor
public class PatientAuthController {

  private final AuthService authService;

  @PostMapping("/send-otp")
  public ResponseEntity<SendOtpResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
    return ResponseEntity.ok(authService.sendPatientOtp(request.phoneNumber()));
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
    return ResponseEntity.ok(authService.verifyPatientOtp(request.phoneNumber(), request.otp()));
  }
}
