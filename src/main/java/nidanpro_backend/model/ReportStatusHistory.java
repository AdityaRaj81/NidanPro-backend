package nidanpro_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "report_status_history")
public class ReportStatusHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "report_id", nullable = false)
  private LabReport report;

  @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
  @Column(nullable = false)
  private ReportStatus status;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "changed_by")
  private StaffUser changedBy;

  @Column(name = "changed_at", nullable = false, updatable = false)
  private Instant changedAt;

  @Column(length = 1000)
  private String remarks;

  @PrePersist
  void prePersist() {
    this.changedAt = Instant.now();
  }
}