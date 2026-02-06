package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Daily Plan Entity
 * Zeitlich strukturierte Darstellung alltäglicher Tätigkeiten pro Tag.
 * Wird gemeinsam von Pfleger und Patient erstellt.
 */
@Entity
@Table(name = "daily_plan", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"patient_id", "plan_date"})
})
public class DailyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Plannungsdatum darf nicht null sein")
    @Column(nullable = false)
    private LocalDate planDate;

    // Relationen
    @OneToMany(mappedBy = "dailyPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyTask> tasks = new ArrayList<>();

    // Konstruktoren
    public DailyPlan() {
    }

    public DailyPlan(Patient patient, LocalDate planDate) {
        this.patient = patient;
        this.planDate = planDate;
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

    public LocalDate getPlanDate() {
        return planDate;
    }

    public void setPlanDate(LocalDate planDate) {
        this.planDate = planDate;
    }

    public List<DailyTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<DailyTask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "DailyPlan{" +
                "id=" + id +
                ", planDate=" + planDate +
                '}';
    }
}

