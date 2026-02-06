package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.Patient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repository for Patient Entity
 * Handles CRUD and complex queries for Patients.
 */
@ApplicationScoped
public class PatientRepository implements PanacheRepository<Patient> {

    /**
     * Find all active patients (not deleted)
     */
    public List<Patient> findAllActive() {
        return find("deleted", false).list();
    }

    /**
     * Find all deleted patients
     */
    public List<Patient> findAllDeleted() {
        return find("deleted", true).list();
    }

    /**
     * Soft delete a patient
     */
    public void softDelete(Long patientId) {
        Patient patient = findById(patientId);
        if (patient != null) {
            patient.setDeleted(true);
            persistAndFlush(patient);
        }
    }

    /**
     * Find patient by name
     */
    public List<Patient> findByName(String firstname, String lastname) {
        return find("firstname = ?1 and lastname = ?2", firstname, lastname).list();
    }
}

