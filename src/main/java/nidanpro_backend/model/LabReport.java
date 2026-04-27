package nidanpro_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class LabReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String reportCode;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "patient_id", nullable = false)
  private Patient patient;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "test_id", nullable = false)
  private LabTest labTest;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportStatus status;

  @Column(length = 1000)
  private String remarks;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "created_by")
  private StaffUser enteredBy;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "verified_by")
  private StaffUser verifiedBy;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column
  private Instant verifiedAt;

  @PrePersist
  void prePersist() {
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
    if (this.status == null) {
      this.status = ReportStatus.SAMPLE_PENDING;
    }
  }

  @jakarta.persistence.PreUpdate
  void preUpdate() {
    this.updatedAt = Instant.now();
  }
}
