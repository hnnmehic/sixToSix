package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.InterventionTask;
import at.htlleonding.sixtosix.entity.Intervention;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repository for InterventionTask Entity
 * Handles CRUD and queries for Intervention Tasks.
 */
@ApplicationScoped
public class InterventionTaskRepository implements PanacheRepository<InterventionTask> {

    /**
     * Find all tasks for an intervention
     */
    public List<InterventionTask> findByIntervention(Intervention intervention) {
        return find("intervention = ?1 order by id", intervention).list();
    }

    /**
     * Find completed tasks
     */
    public List<InterventionTask> findCompletedByIntervention(Intervention intervention) {
        return find("intervention = ?1 and completed = true", intervention).list();
    }

    /**
     * Find pending tasks
     */
    public List<InterventionTask> findPendingByIntervention(Intervention intervention) {
        return find("intervention = ?1 and completed = false", intervention).list();
    }

    /**
     * Count completed tasks
     */
    public long countCompleted(Intervention intervention) {
        return find("intervention = ?1 and completed = true", intervention).count();
    }

    /**
     * Count total tasks
     */
    public long countTotal(Intervention intervention) {
        return find("intervention = ?1", intervention).count();
    }
}

