package at.htlleonding.sixtosix.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO für ADLAssessment (Record)
 * Bewertung von Aktivitäten des täglichen Lebens.
 * Immutable und kompakt mit Java Records.
 */
public record ADLAssessmentDTO(
    Long id,

    @NotNull(message = "ADL Definition ID darf nicht null sein")
    Long adlDefinitionId,

    String adlName,

    @NotNull(message = "Patient ID darf nicht null sein")
    Long patientId,

    @NotNull(message = "Status darf nicht null sein")
    String status,

    LocalDateTime assessedAt,
    Long assessedById,
    String assessedByName
) {
}

