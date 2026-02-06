package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Intervention Entity
 * Zentrale Planungseinheit der Pflege.
 * Basiert auf: Anamnesis, ADLs, Skills, Ressourcen, Bedarf.
 * Kann Pflegehandlungen, Beobachtungen und grafische Behandlungsanleitungen enthalten.
 */
@Entity
@Table(name = "intervention")
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Source darf nicht null sein")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterventionSource source;

    @NotBlank(message = "Titel darf nicht leer sein")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationen
    @OneToMany(mappedBy = "intervention", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterventionTask> tasks = new ArrayList<>();

    // Konstruktoren
    public Intervention() {
    }

    public Intervention(Patient patient, InterventionSource source, String title) {
        this.patient = patient;
        this.source = source;
        this.title = title;
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

    public InterventionSource getSource() {
        return source;
    }

    public void setSource(InterventionSource source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<InterventionTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<InterventionTask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Intervention{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}

