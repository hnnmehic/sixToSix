package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Medication Entity
 * Medikamentenverwaltung pro Patient.
 * Rein informativ, keine automatischen Erinnerungen.
 */
@Entity
@Table(name = "medication")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotBlank(message = "Medikamentenname darf nicht leer sein")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Dosierung darf nicht leer sein")
    @Column(nullable = false)
    private String dosage;

    @NotBlank(message = "Einnahmezeitpunkt darf nicht leer sein")
    @Column(nullable = false)
    private String intakeTime;

    @Column(nullable = false)
    private Boolean confirmable = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Konstruktoren
    public Medication() {
    }

    public Medication(Patient patient, String name, String dosage, String intakeTime) {
        this.patient = patient;
        this.name = name;
        this.dosage = dosage;
        this.intakeTime = intakeTime;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getIntakeTime() {
        return intakeTime;
    }

    public void setIntakeTime(String intakeTime) {
        this.intakeTime = intakeTime;
    }

    public Boolean getConfirmable() {
        return confirmable;
    }

    public void setConfirmable(Boolean confirmable) {
        this.confirmable = confirmable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Medication{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dosage='" + dosage + '\'' +
                ", intakeTime='" + intakeTime + '\'' +
                '}';
    }
}

