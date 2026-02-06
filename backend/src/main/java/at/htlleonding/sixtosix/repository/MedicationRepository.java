package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.Medication;
import at.htlleonding.sixtosix.entity.Patient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repository for Medication Entity
 * Handles CRUD and queries for Medications.
 */
@ApplicationScoped
public class MedicationRepository implements PanacheRepository<Medication> {

    /**
     * Find all medications for a patient
     */
    public List<Medication> findByPatient(Patient patient) {
        return find("patient = ?1 order by createdAt desc", patient).list();
    }

    /**
     * Find confirmable medications
     */
    public List<Medication> findConfirmableByPatient(Patient patient) {
        return find("patient = ?1 and confirmable = true order by createdAt desc", patient).list();
    }

    /**
     * Find medications by intake time
     */
    public List<Medication> findByPatientAndIntakeTime(Patient patient, String intakeTime) {
        return find("patient = ?1 and intakeTime = ?2 order by createdAt", patient, intakeTime).list();
    }
}

