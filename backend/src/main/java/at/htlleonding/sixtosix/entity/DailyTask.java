package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Daily Task Entity
 * Einzelne Aktivitäten einer Tagesstruktur (z.B. Trinken, Bewegung, Entspannung).
 * Können manuell bestätigt werden.
 */
@Entity
@Table(name = "daily_task")
public class DailyTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Daily Plan darf nicht null sein")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "daily_plan_id", nullable = false)
    private DailyPlan dailyPlan;

    @NotBlank(message = "Titel darf nicht leer sein")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Reminder Level darf nicht null sein")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderLevel reminderLevel;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(updatable = false)
    private LocalDateTime confirmedAt;

    // Konstruktoren
    public DailyTask() {
    }

    public DailyTask(DailyPlan dailyPlan, String title, ReminderLevel reminderLevel) {
        this.dailyPlan = dailyPlan;
        this.title = title;
        this.reminderLevel = reminderLevel;
    }

    // Business Methods
    public void confirm() {
        this.completed = true;
        this.confirmedAt = LocalDateTime.now();
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DailyPlan getDailyPlan() {
        return dailyPlan;
    }

    public void setDailyPlan(DailyPlan dailyPlan) {
        this.dailyPlan = dailyPlan;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ReminderLevel getReminderLevel() {
        return reminderLevel;
    }

    public void setReminderLevel(ReminderLevel reminderLevel) {
        this.reminderLevel = reminderLevel;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    @Override
    public String toString() {
        return "DailyTask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", reminderLevel=" + reminderLevel +
                ", completed=" + completed +
                '}';
    }
}

