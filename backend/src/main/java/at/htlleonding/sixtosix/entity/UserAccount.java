package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User Account Entity
 * Zentrale Verwaltung von Benutzern (Pfleger).
 * Die Authentifizierung erfolgt über Keycloak.
 */
@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Keycloak ID darf nicht leer sein")
    @Column(unique = true, nullable = false)
    private String keycloakId;

    @NotNull(message = "Rolle darf nicht null sein")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationen
    @OneToMany(mappedBy = "pfleger", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareAssignment> careAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.REFRESH)
    private List<AnamnesisVersion> anamnesisVersions = new ArrayList<>();

    @OneToMany(mappedBy = "assessedBy", cascade = CascadeType.REFRESH)
    private List<ADLAssessment> adlAssessments = new ArrayList<>();

    @OneToMany(mappedBy = "proposedBy", cascade = CascadeType.REFRESH)
    private List<Resource> resources = new ArrayList<>();

    @OneToMany(mappedBy = "performedBy", cascade = CascadeType.REFRESH)
    private List<AuditLog> auditLogs = new ArrayList<>();

    // Konstruktoren
    public UserAccount() {
    }

    public UserAccount(String keycloakId, UserRole role) {
        this.keycloakId = keycloakId;
        this.role = role;
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

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CareAssignment> getCareAssignments() {
        return careAssignments;
    }

    public void setCareAssignments(List<CareAssignment> careAssignments) {
        this.careAssignments = careAssignments;
    }

    public List<AnamnesisVersion> getAnamnesisVersions() {
        return anamnesisVersions;
    }

    public void setAnamnesisVersions(List<AnamnesisVersion> anamnesisVersions) {
        this.anamnesisVersions = anamnesisVersions;
    }

    public List<ADLAssessment> getAdlAssessments() {
        return adlAssessments;
    }

    public void setAdlAssessments(List<ADLAssessment> adlAssessments) {
        this.adlAssessments = adlAssessments;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(List<AuditLog> auditLogs) {
        this.auditLogs = auditLogs;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "id=" + id +
                ", keycloakId='" + keycloakId + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}

