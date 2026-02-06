package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.DailyPlan;
import at.htlleonding.sixtosix.entity.Patient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for DailyPlan Entity
 * Handles CRUD and queries for Daily Plans.
 */
@ApplicationScoped
public class DailyPlanRepository implements PanacheRepository<DailyPlan> {

    /**
     * Find plan for a specific date
     */
    public Optional<DailyPlan> findByPatientAndDate(Patient patient, LocalDate date) {
        return find("patient = ?1 and planDate = ?2", patient, date).firstResultOptional();
    }

    /**
     * Find all plans for a patient
     */
    public List<DailyPlan> findByPatient(Patient patient) {
        return find("patient = ?1 order by planDate desc", patient).list();
    }

    /**
     * Find plans within date range
     */
    public List<DailyPlan> findByPatientAndDateRange(Patient patient, LocalDate startDate, LocalDate endDate) {
        return find("patient = ?1 and planDate between ?2 and ?3 order by planDate desc",
                patient, startDate, endDate).list();
    }

    /**
     * Find today's plan
     */
    public Optional<DailyPlan> findTodaysPlan(Patient patient) {
        return findByPatientAndDate(patient, LocalDate.now());
    }
}

