package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.DailyTask;
import at.htlleonding.sixtosix.entity.DailyPlan;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repository for DailyTask Entity
 * Handles CRUD and queries for Daily Tasks.
 */
@ApplicationScoped
public class DailyTaskRepository implements PanacheRepository<DailyTask> {

    /**
     * Find all tasks for a daily plan
     */
    public List<DailyTask> findByDailyPlan(DailyPlan dailyPlan) {
        return find("dailyPlan = ?1 order by id", dailyPlan).list();
    }

    /**
     * Find completed tasks
     */
    public List<DailyTask> findCompletedByDailyPlan(DailyPlan dailyPlan) {
        return find("dailyPlan = ?1 and completed = true", dailyPlan).list();
    }

    /**
     * Find pending tasks
     */
    public List<DailyTask> findPendingByDailyPlan(DailyPlan dailyPlan) {
        return find("dailyPlan = ?1 and completed = false", dailyPlan).list();
    }

    /**
     * Count completed tasks
     */
    public long countCompleted(DailyPlan dailyPlan) {
        return find("dailyPlan = ?1 and completed = true", dailyPlan).count();
    }

    /**
     * Count total tasks
     */
    public long countTotal(DailyPlan dailyPlan) {
        return find("dailyPlan = ?1", dailyPlan).count();
    }
}

