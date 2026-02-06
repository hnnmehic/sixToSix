package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.CareAssignment;
import at.htlleonding.sixtosix.entity.Patient;
import at.htlleonding.sixtosix.entity.UserAccount;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

/**
 * Repository for CareAssignment Entity
 * Handles CRUD and complex queries for Care Assignments.
 */
@ApplicationScoped
public class CareAssignmentRepository implements PanacheRepository<CareAssignment> {

    /**
     * Find assignment between specific pfleger and patient
     */
    public Optional<CareAssignment> findByPflegerAndPatient(UserAccount pfleger, Patient patient) {
        return find("pfleger = ?1 and patient = ?2", pfleger, patient).firstResultOptional();
    }

    /**
     * Find all active assignments for a patient
     */
    public List<CareAssignment> findActiveByPatient(Patient patient) {
        return find("patient = ?1 and active = true", patient).list();
    }

    /**
     * Find all active assignments for a pfleger
     */
    public List<CareAssignment> findActiveByPfleger(UserAccount pfleger) {
        return find("pfleger = ?1 and active = true", pfleger).list();
    }

    /**
     * Check if pfleger is assigned to patient
     */
    public boolean isAssignedAndActive(UserAccount pfleger, Patient patient) {
        return find("pfleger = ?1 and patient = ?2 and active = true", pfleger, patient).count() > 0;
    }

    /**
     * Deactivate assignment
     */
    public void deactivateAssignment(Long assignmentId) {
        CareAssignment assignment = findById(assignmentId);
        if (assignment != null) {
            assignment.setActive(false);
            persistAndFlush(assignment);
        }
    }
}

