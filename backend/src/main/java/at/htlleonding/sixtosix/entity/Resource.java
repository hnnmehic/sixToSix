package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Resource Entity
 * Soziale, persönliche und biografische Faktoren des Patienten.
 * Beispiele: Familie, Freunde, Beruf, Vorlieben.
 * Patienten können Ressourcen vorschlagen, Pfleger bestätigen diese.
 */
@Entity
@Table(name = "resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotBlank(message = "Typ darf nicht leer sein")
    @Column(nullable = false)
    private String type;

    @NotBlank(message = "Beschreibung darf nicht leer sein")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposed_by")
    private UserAccount proposedBy;

    @Column(nullable = false)
    private Boolean approved = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Konstruktoren
    public Resource() {
    }

    public Resource(Patient patient, String type, String description) {
        this.patient = patient;
        this.type = type;
        this.description = description;
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserAccount getProposedBy() {
        return proposedBy;
    }

    public void setProposedBy(UserAccount proposedBy) {
        this.proposedBy = proposedBy;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", approved=" + approved +
                ", createdAt=" + createdAt +
                '}';
    }
}

