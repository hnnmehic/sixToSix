package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Intervention Task Entity
 * Einzelne Aufgaben/Tätigkeiten einer Intervention.
 * Können als durchgeführt markiert werden.
 */
@Entity
@Table(name = "intervention_task")
public class InterventionTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Intervention darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intervention_id", nullable = false)
    private Intervention intervention;

    @NotBlank(message = "Beschreibung darf nicht leer sein")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(updatable = false)
    private LocalDateTime completedAt;

    // Konstruktoren
    public InterventionTask() {
    }

    public InterventionTask(Intervention intervention, String description) {
        this.intervention = intervention;
        this.description = description;
    }

    // Business Methods
    public void markCompleted() {
        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public String toString() {
        return "InterventionTask{" +
                "id=" + id +
                ", completed=" + completed +
                ", completedAt=" + completedAt +
                '}';
    }
}

