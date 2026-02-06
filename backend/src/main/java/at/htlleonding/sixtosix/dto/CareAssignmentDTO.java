package at.htlleonding.sixtosix.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO für CareAssignment (Record)
 * Repräsentiert die Zuordnung zwischen Pfleger und Patient.
 * Immutable und kompakt mit Java Records.
 */
public record CareAssignmentDTO(
    Long id,

    @NotNull(message = "Pfleger ID darf nicht null sein")
    Long pflegerUserId,

    String pflegerName,

    @NotNull(message = "Patient ID darf nicht null sein")
    Long patientId,

    String patientName,

    Boolean active,
    LocalDateTime createdAt
) {
}

