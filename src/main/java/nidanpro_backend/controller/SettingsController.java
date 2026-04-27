package nidanpro_backend.controller;

import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.LabSettingsRequest;
import nidanpro_backend.model.LabSetting;
import nidanpro_backend.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

  private final SettingsService settingsService;

  @GetMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','PATHOLOGIST')")
  public ResponseEntity<LabSetting> getSettings() {
    return ResponseEntity.ok(settingsService.getSettings());
  }

  @PutMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<LabSetting> save(@RequestBody LabSettingsRequest request) {
    return ResponseEntity.ok(settingsService.saveSettings(request));
  }
}
