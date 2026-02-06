package at.htlleonding.sixtosix.dto;

import java.time.LocalDateTime;

/**
 * DTO für CareNeed (Record)
 * Ad-hoc erfasste Bedarf-/Krisensituationen.
 * Immutable und kompakt mit Java Records.
 */
public record CareNeedDTO(
    Long id,
    Long patientId,
    String description,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime resolvedAt
) {
}

