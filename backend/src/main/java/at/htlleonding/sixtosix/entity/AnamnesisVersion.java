package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * Anamnesis Version Entity
 * Änderungen werden versioniert.
 * Alte Versionen bleiben nachvollziehbar.
 * Nach Finalisierung nicht mehr änderbar.
 */
@Entity
@Table(name = "anamnesis_version")
public class AnamnesisVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Anamnesis darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anamnesis_id", nullable = false)
    private Anamnesis anamnesis;

    @NotNull(message = "Versionsnummer darf nicht null sein")
    @Positive(message = "Versionsnummer muss positiv sein")
    @Column(nullable = false)
    private Long versionNumber;

    @NotNull(message = "Inhalt darf nicht null sein")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull(message = "Ersteller darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private UserAccount createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean finalized = false;

    // Konstruktoren
    public AnamnesisVersion() {
    }

    public AnamnesisVersion(Anamnesis anamnesis, Long versionNumber, String content, UserAccount createdBy) {
        this.anamnesis = anamnesis;
        this.versionNumber = versionNumber;
        this.content = content;
        this.createdBy = createdBy;
    }

    // Lifecycle
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Business Methods
    public void finalize() {
        this.finalized = true;
    }

    public boolean canEdit() {
        return !finalized;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Anamnesis getAnamnesis() {
        return anamnesis;
    }

    public void setAnamnesis(Anamnesis anamnesis) {
        this.anamnesis = anamnesis;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserAccount getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserAccount createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getFinalized() {
        return finalized;
    }

    public void setFinalized(Boolean finalized) {
        this.finalized = finalized;
    }

    @Override
    public String toString() {
        return "AnamnesisVersion{" +
                "id=" + id +
                ", versionNumber=" + versionNumber +
                ", createdAt=" + createdAt +
                ", finalized=" + finalized +
                '}';
    }
}

