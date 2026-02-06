package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.SkillAssessment;
import at.htlleonding.sixtosix.entity.Patient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repository for SkillAssessment Entity
 * Handles CRUD and queries for Skill Assessments.
 */
@ApplicationScoped
public class SkillAssessmentRepository implements PanacheRepository<SkillAssessment> {

    /**
     * Find all assessments for a patient
     */
    public List<SkillAssessment> findByPatient(Patient patient) {
        return find("patient = ?1 order by assessedAt desc", patient).list();
    }

    /**
     * Find latest assessment per skill for a patient
     */
    public List<SkillAssessment> findLatestByPatient(Patient patient) {
        return find("SELECT s FROM SkillAssessment s WHERE s.patient = ?1 " +
                "AND s.assessedAt = (SELECT MAX(s2.assessedAt) FROM SkillAssessment s2 " +
                "WHERE s2.patient = ?1 AND s2.skillDefinition = s.skillDefinition)",
                patient).list();
    }

    /**
     * Find assessments by patient and status
     */
    public List<SkillAssessment> findByPatientAndStatus(Patient patient, String status) {
        return find("patient = ?1 and status = ?2 order by assessedAt desc", patient, status).list();
    }
}

