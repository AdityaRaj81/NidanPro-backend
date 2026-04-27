package nidanpro_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "report_values")
public class ReportResult {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "report_id", nullable = false)
  private LabReport report;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "parameter_id", nullable = false)
  private TestParameter parameter;

  @Column(nullable = false)
  private String value;
}
