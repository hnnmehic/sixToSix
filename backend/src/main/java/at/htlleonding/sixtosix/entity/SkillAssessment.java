package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Skill Assessment Entity
 * Bewertung von Fähigkeitsbereichen pro Patient.
 */
@Entity
@Table(name = "skill_assessment")
public class SkillAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Skill Definition darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_definition_id", nullable = false)
    private SkillDefinition skillDefinition;

    @NotNull(message = "Patient darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Status darf nicht null sein")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentStatus status;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assessedAt;

    // Konstruktoren
    public SkillAssessment() {
    }

    public SkillAssessment(SkillDefinition skillDefinition, Patient patient,
                           AssessmentStatus status) {
        this.skillDefinition = skillDefinition;
        this.patient = patient;
        this.status = status;
    }

    // Lifecycle
    @PrePersist
    protected void onCreate() {
        assessedAt = LocalDateTime.now();
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SkillDefinition getSkillDefinition() {
        return skillDefinition;
    }

    public void setSkillDefinition(SkillDefinition skillDefinition) {
        this.skillDefinition = skillDefinition;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(LocalDateTime assessedAt) {
        this.assessedAt = assessedAt;
    }

    @Override
    public String toString() {
        return "SkillAssessment{" +
                "id=" + id +
                ", status=" + status +
                ", assessedAt=" + assessedAt +
                '}';
    }
}

