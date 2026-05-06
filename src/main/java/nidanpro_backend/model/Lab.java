package nidanpro_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "labs")
public class Lab {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "lab_name", nullable = false)
  private String labName;

  @Column(unique = true)
  private String subdomain;

  @Column(name = "custom_domain", unique = true)
  private String customDomain;

  @Column(name = "logo_url", length = 4000)
  private String logoUrl;

  @Column(name = "primary_color")
  private String primaryColor;

  @Column(name = "secondary_color")
  private String secondaryColor;

  @Column(name = "subscription_plan", nullable = false)
  private String subscriptionPlan;

  @Column(name = "subscription_expiry")
  private LocalDate subscriptionExpiry;

  @Column(name = "payment_status", nullable = false)
  private String paymentStatus;

  @Column(name = "is_active", nullable = false)
  private boolean active = true;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  void prePersist() {
    this.createdAt = Instant.now();
  }
}
