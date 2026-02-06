package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * ADL Assessment Entity
 * Bewertung von Aktivitäten des täglichen Lebens pro Patient.
 * Der zeitliche Verlauf ist nachvollziehbar (multiple Bewertungen möglich).
 */
@Entity
@Table(name = "adl_assessment")
public class ADLAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ADL Definition darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adl_definition_id", nullable = false)
    private ADLDefinition adlDefinition;

    @NotNull(message = "Patient darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Status darf nicht null sein")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assessedAt;

    @NotNull(message = "Bewerter darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessed_by", nullable = false)
    private UserAccount assessedBy;

    // Konstruktoren
    public ADLAssessment() {
    }

    public ADLAssessment(ADLDefinition adlDefinition, Patient patient,
                         AssessmentStatus status, UserAccount assessedBy) {
        this.adlDefinition = adlDefinition;
        this.patient = patient;
        this.status = status;
        this.assessedBy = assessedBy;
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

    public ADLDefinition getAdlDefinition() {
        return adlDefinition;
    }

    public void setAdlDefinition(ADLDefinition adlDefinition) {
        this.adlDefinition = adlDefinition;
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

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(LocalDateTime assessedAt) {
        this.assessedAt = assessedAt;
    }

    public UserAccount getAssessedBy() {
        return assessedBy;
    }

    public void setAssessedBy(UserAccount assessedBy) {
        this.assessedBy = assessedBy;
    }

    @Override
    public String toString() {
        return "ADLAssessment{" +
                "id=" + id +
                ", status=" + status +
                ", assessedAt=" + assessedAt +
                '}';
    }
}

