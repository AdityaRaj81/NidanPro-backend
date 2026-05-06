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
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test_parameters")
public class TestParameter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "test_id", nullable = false)
  private LabTest labTest;

  @Column(nullable = false)
  private String parameterName;

  @Column(nullable = false)
  private String unit;

  @Enumerated(EnumType.STRING)
  @Column(name = "range_rule_type", nullable = false)
  private RangeRuleType rangeRuleType = RangeRuleType.BETWEEN;

  @Column(name = "lower_bound", precision = 16, scale = 4)
  private BigDecimal lowerBound;

  @Column(name = "upper_bound", precision = 16, scale = 4)
  private BigDecimal upperBound;

  @Column(name = "reference_range", nullable = false)
  private String referenceRange;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  void prePersist() {
    this.createdAt = Instant.now();
  }
}
