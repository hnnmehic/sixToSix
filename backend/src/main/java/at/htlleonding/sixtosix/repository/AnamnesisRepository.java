package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.Anamnesis;
import at.htlleonding.sixtosix.entity.Patient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

/**
 * Repository for Anamnesis Entity
 * Handles CRUD and queries for Anamnesis.
 */
@ApplicationScoped
public class AnamnesisRepository implements PanacheRepository<Anamnesis> {

    /**
     * Find anamnesis by patient
     */
    public Optional<Anamnesis> findByPatient(Patient patient) {
        return find("patient", patient).firstResultOptional();
    }

    /**
     * Check if anamnesis exists for patient
     */
    public boolean existsByPatient(Patient patient) {
        return find("patient", patient).count() > 0;
    }
}

