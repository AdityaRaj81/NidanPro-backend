package nidanpro_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "lab_settings")
public class LabSetting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String labName;

  @Column(length = 1200)
  private String address;

  @Column
  private String phone;

  @Column
  private String email;

  @Column
  private String website;

  @Column(length = 4000)
  private String logoUrl;

  @Column(length = 4000)
  private String signatureUrl;
}
