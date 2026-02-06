package at.htlleonding.sixtosix.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO für Intervention (Record)
 * Zentrale Planungseinheit der Pflege.
 * Immutable und kompakt mit Java Records.
 */
public record InterventionDTO(
    Long id,
    Long patientId,
    String source,
    String title,
    String description,
    Boolean active,
    LocalDateTime createdAt,
    List<InterventionTaskDTO> tasks
) {
}

