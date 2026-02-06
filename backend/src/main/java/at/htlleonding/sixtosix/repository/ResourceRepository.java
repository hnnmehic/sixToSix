package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.Resource;
import at.htlleonding.sixtosix.entity.Patient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repository for Resource Entity
 * Handles CRUD and queries for Resources.
 */
@ApplicationScoped
public class ResourceRepository implements PanacheRepository<Resource> {

    /**
     * Find all resources for a patient
     */
    public List<Resource> findByPatient(Patient patient) {
        return find("patient = ?1 order by createdAt desc", patient).list();
    }

    /**
     * Find approved resources for a patient
     */
    public List<Resource> findApprovedByPatient(Patient patient) {
        return find("patient = ?1 and approved = true order by createdAt desc", patient).list();
    }

    /**
     * Find pending resources for a patient (not approved yet)
     */
    public List<Resource> findPendingByPatient(Patient patient) {
        return find("patient = ?1 and approved = false order by createdAt desc", patient).list();
    }

    /**
     * Find resources by type
     */
    public List<Resource> findByPatientAndType(Patient patient, String type) {
        return find("patient = ?1 and type = ?2 order by createdAt desc", patient, type).list();
    }
}

