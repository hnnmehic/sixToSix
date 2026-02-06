package at.htlleonding.sixtosix.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO für Patient mit vollständigen Daten (Record)
 * Wird für detaillierte GET Requests verwendet.
 * Immutable und kompakt mit Java Records.
 */
public record PatientDetailDTO(
    Long id,
    String firstname,
    String lastname,
    LocalDate birthdate,
    Boolean deleted,
    List<CareAssignmentDTO> careAssignments,
    AnamnesisDTO anamnesis,
    List<ADLAssessmentDTO> adlAssessments,
    List<ResourceDTO> resources,
    List<InterventionDTO> interventions,
    List<MedicationDTO> medications
) {
}

