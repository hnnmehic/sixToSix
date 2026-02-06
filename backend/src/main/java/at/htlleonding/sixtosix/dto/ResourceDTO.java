package at.htlleonding.sixtosix.dto;

import java.time.LocalDateTime;

/**
 * DTO für Resource (Record)
 * Soziale, persönliche und biografische Faktoren des Patienten.
 * Immutable und kompakt mit Java Records.
 */
public record ResourceDTO(
    Long id,
    Long patientId,
    String type,
    String description,
    Long proposedById,
    String proposedByName,
    Boolean approved,
    LocalDateTime createdAt
) {
}

