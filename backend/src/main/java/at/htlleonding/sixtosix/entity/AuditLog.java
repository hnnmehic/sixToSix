package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Audit Log Entity
 * Protokolliert alle Änderungen für Revisionssicherheit.
 * Immutable Record für Nachvollziehbarkeit.
 */
@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Entity-Typ darf nicht leer sein")
    @Column(nullable = false)
    private String entity;

    @NotNull(message = "Entity ID darf nicht null sein")
    @Column(nullable = false)
    private Long entityId;

    @NotNull(message = "Action darf nicht null sein")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @NotNull(message = "Performer darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by", nullable = false)
    private UserAccount performedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime performedAt;

    @Column(columnDefinition = "TEXT")
    private String details;

    // Konstruktoren
    public AuditLog() {
    }

    public AuditLog(String entity, Long entityId, AuditAction action, UserAccount performedBy) {
        this.entity = entity;
        this.entityId = entityId;
        this.action = action;
        this.performedBy = performedBy;
    }

    // Lifecycle
    @PrePersist
    protected void onCreate() {
        performedAt = LocalDateTime.now();
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public UserAccount getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(UserAccount performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", entity='" + entity + '\'' +
                ", entityId=" + entityId +
                ", action=" + action +
                ", performedAt=" + performedAt +
                '}';
    }
}

