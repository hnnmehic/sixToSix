package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Anamnesis Entity
 * Pro Patient existiert eine globale Anamnese.
 * Die Anamnese ist laufend erweiterbar.
 */
@Entity
@Table(name = "anamnesis")
public class Anamnesis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient darf nicht null sein")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationen
    @OneToMany(mappedBy = "anamnesis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnamnesisVersion> versions = new ArrayList<>();

    // Konstruktoren
    public Anamnesis() {
    }

    public Anamnesis(Patient patient) {
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<AnamnesisVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<AnamnesisVersion> versions) {
        this.versions = versions;
    }

    @Override
    public String toString() {
        return "Anamnesis{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                '}';
    }
}

