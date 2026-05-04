package nidanpro_backend.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.AuthResponse;
import nidanpro_backend.dto.PatientSummaryResponse;
import nidanpro_backend.dto.SendOtpResponse;
import nidanpro_backend.dto.VerifyOtpResponse;
import nidanpro_backend.repository.PatientRepository;
import nidanpro_backend.repository.StaffUserRepository;
import nidanpro_backend.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final StaffUserRepository staffUserRepository;
  private final PatientRepository patientRepository;
  private final JwtService jwtService;

  @Value("${app.patient-auth.default-otp}")
  private String defaultOtp;

  public AuthResponse loginStaff(String loginId, String password, String role) {
    String normalizedId = loginId.trim();
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedId, password));

    nidanpro_backend.model.StaffUser staff;
    if (normalizedId.contains("@")) {
        staff = staffUserRepository.findByEmailIgnoreCase(normalizedId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    } else {
        staff = staffUserRepository.findByEmployeeCode(normalizedId.toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    }

    if (role != null && !role.isBlank() && !staff.getRole().getRoleName().equalsIgnoreCase(role)) {
        throw new IllegalArgumentException("Selected role does not match user account");
    }

    var principal = User.builder()
        .username(normalizedId)
        .password(staff.getPasswordHash())
      .roles(staff.getRole().getRoleName())
        .build();

    String token = jwtService.generateToken(
        principal,
      Map.of("role", staff.getRole().getRoleName(), "staffId", staff.getId()));

    return new AuthResponse(token, staff.getEmail(), staff.getName(), staff.getRole().getRoleName());
  }

  public SendOtpResponse sendPatientOtp(String phoneNumber) {
    String normalizedPhone = phoneNumber.replaceAll("\\D", "");
    if (normalizedPhone.length() < 10) {
      throw new IllegalArgumentException("Invalid phone number");
    }

    return new SendOtpResponse(
        "OTP service disabled. Use the default OTP for login.",
        defaultOtp);
  }

  public VerifyOtpResponse verifyPatientOtp(String phoneNumber, String otp) {
    String normalizedPhone = phoneNumber.replaceAll("\\D", "");

    if (!defaultOtp.equals(otp)) {
      throw new IllegalArgumentException("Invalid OTP");
    }

    List<PatientSummaryResponse> patients = patientRepository.findByPhoneNumber(normalizedPhone)
        .stream()
        .map(p -> new PatientSummaryResponse(
            p.getId(),
            p.getPatientCode(),
            p.getFullName(),
            p.getAge(),
            p.getGender(),
            p.getPhoneNumber()))
        .toList();

    return new VerifyOtpResponse("OTP verified", patients);
  }
}
