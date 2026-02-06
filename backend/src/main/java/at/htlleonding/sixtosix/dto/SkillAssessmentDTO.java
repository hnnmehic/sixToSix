package at.htlleonding.sixtosix.dto;

import java.time.LocalDateTime;

/**
 * DTO für SkillAssessment (Record)
 * Bewertung von Fähigkeitsbereichen pro Patient.
 * Immutable und kompakt mit Java Records.
 */
public record SkillAssessmentDTO(
    Long id,
    Long skillDefinitionId,
    String skillName,
    Long patientId,
    String status,
    String comment,
    LocalDateTime assessedAt
) {
}

