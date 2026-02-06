package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.CareNeed;
import at.htlleonding.sixtosix.entity.Patient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repository for CareNeed Entity
 * Handles CRUD and queries for Care Needs.
 */
@ApplicationScoped
public class CareNeedRepository implements PanacheRepository<CareNeed> {

    /**
     * Find all care needs for a patient
     */
    public List<CareNeed> findByPatient(Patient patient) {
        return find("patient = ?1 order by createdAt desc", patient).list();
    }

    /**
     * Find active care needs for a patient
     */
    public List<CareNeed> findActiveByPatient(Patient patient) {
        return find("patient = ?1 and active = true order by createdAt desc", patient).list();
    }

    /**
     * Find resolved care needs for a patient
     */
    public List<CareNeed> findResolvedByPatient(Patient patient) {
        return find("patient = ?1 and active = false order by resolvedAt desc", patient).list();
    }

    /**
     * Resolve a care need
     */
    public void resolveNeed(Long careNeedId) {
        CareNeed careNeed = findById(careNeedId);
        if (careNeed != null) {
            careNeed.resolve();
            persistAndFlush(careNeed);
        }
    }
}

