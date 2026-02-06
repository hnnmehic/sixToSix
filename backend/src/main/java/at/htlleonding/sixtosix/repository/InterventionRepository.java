package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.Intervention;
import at.htlleonding.sixtosix.entity.Patient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repository for Intervention Entity
 * Handles CRUD and queries for Interventions.
 */
@ApplicationScoped
public class InterventionRepository implements PanacheRepository<Intervention> {

    /**
     * Find all interventions for a patient
     */
    public List<Intervention> findByPatient(Patient patient) {
        return find("patient = ?1 order by createdAt desc", patient).list();
    }

    /**
     * Find active interventions for a patient
     */
    public List<Intervention> findActiveByPatient(Patient patient) {
        return find("patient = ?1 and active = true order by createdAt desc", patient).list();
    }

    /**
     * Find inactive interventions for a patient
     */
    public List<Intervention> findInactiveByPatient(Patient patient) {
        return find("patient = ?1 and active = false order by createdAt desc", patient).list();
    }

    /**
     * Find interventions by source
     */
    public List<Intervention> findByPatientAndSource(Patient patient, String source) {
        return find("patient = ?1 and source = ?2 order by createdAt desc", patient, source).list();
    }

    /**
     * Deactivate intervention
     */
    public void deactivateIntervention(Long interventionId) {
        Intervention intervention = findById(interventionId);
        if (intervention != null) {
            intervention.setActive(false);
            persistAndFlush(intervention);
        }
    }
}

