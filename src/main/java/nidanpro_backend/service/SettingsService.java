package nidanpro_backend.service;

import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.LabSettingsRequest;
import nidanpro_backend.model.LabSetting;
import nidanpro_backend.repository.LabSettingRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

  private final LabSettingRepository labSettingRepository;

  public LabSetting getSettings() {
    return labSettingRepository.findAll().stream().findFirst().orElseGet(() -> {
      LabSetting setting = new LabSetting();
      setting.setLabName("NidanPro");
      return labSettingRepository.save(setting);
    });
  }

  public LabSetting saveSettings(LabSettingsRequest request) {
    LabSetting current = getSettings();
    current.setLabName(request.labName());
    current.setAddress(request.address());
    current.setPhone(request.phone());
    current.setEmail(request.email());
    current.setWebsite(request.website());
    current.setLogoUrl(request.logoUrl());
    current.setSignatureUrl(request.signatureUrl());
    return labSettingRepository.save(current);
  }
}
