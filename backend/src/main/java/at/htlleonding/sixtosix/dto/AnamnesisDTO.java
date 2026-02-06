package at.htlleonding.sixtosix.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO für Anamnesis (Record)
 * Globale Patientenanamnese mit Versionsverlauf.
 * Immutable und kompakt mit Java Records.
 */
public record AnamnesisDTO(
    Long id,

    @NotNull(message = "Patient ID darf nicht null sein")
    Long patientId,

    LocalDateTime createdAt,
    List<AnamnesisVersionDTO> versions
) {
}

