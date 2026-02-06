package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Care Assignment Entity
 * Zuordnung zwischen Pfleger und Patient.
 * Regelt die Betreuungsbeziehung.
 */
@Entity
@Table(name = "care_assignment", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"pfleger_id", "patient_id"})
})
public class CareAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pfleger darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pfleger_id", nullable = false)
    private UserAccount pfleger;

    @NotNull(message = "Patient darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Konstruktoren
    public CareAssignment() {
    }

    public CareAssignment(UserAccount pfleger, Patient patient) {
        this.pfleger = pfleger;
        this.patient = patient;
    }

    // Lifecycle
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getPfleger() {
        return pfleger;
    }

    public void setPfleger(UserAccount pfleger) {
        this.pfleger = pfleger;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CareAssignment{" +
                "id=" + id +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}

