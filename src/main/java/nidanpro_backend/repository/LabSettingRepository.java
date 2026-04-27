package nidanpro_backend.repository;

import nidanpro_backend.model.LabSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabSettingRepository extends JpaRepository<LabSetting, Long> {
}
