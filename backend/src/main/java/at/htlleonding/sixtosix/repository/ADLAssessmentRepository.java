package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.ADLAssessment;
import at.htlleonding.sixtosix.entity.Patient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repository for ADLAssessment Entity
 * Handles CRUD and queries for ADL Assessments.
 */
@ApplicationScoped
public class ADLAssessmentRepository implements PanacheRepository<ADLAssessment> {

    /**
     * Find all assessments for a patient
     */
    public List<ADLAssessment> findByPatient(Patient patient) {
        return find("patient = ?1 order by assessedAt desc", patient).list();
    }

    /**
     * Find latest assessment per ADL for a patient
     */
    public List<ADLAssessment> findLatestByPatient(Patient patient) {
        return find("SELECT a FROM ADLAssessment a WHERE a.patient = ?1 " +
                "AND a.assessedAt = (SELECT MAX(a2.assessedAt) FROM ADLAssessment a2 " +
                "WHERE a2.patient = ?1 AND a2.adlDefinition = a.adlDefinition)",
                patient).list();
    }

    /**
     * Find assessments by patient and status
     */
    public List<ADLAssessment> findByPatientAndStatus(Patient patient, String status) {
        return find("patient = ?1 and status = ?2 order by assessedAt desc", patient, status).list();
    }
}

