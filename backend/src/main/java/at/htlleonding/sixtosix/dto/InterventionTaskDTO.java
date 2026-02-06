package at.htlleonding.sixtosix.dto;

/**
 * DTO für InterventionTask (Record)
 * Einzelne Aufgaben/Tätigkeiten einer Intervention.
 * Immutable und kompakt mit Java Records.
 */
public record InterventionTaskDTO(
    Long id,
    Long interventionId,
    String description,
    Boolean completed
) {
}

