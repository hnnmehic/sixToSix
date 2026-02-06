package at.htlleonding.sixtosix.dto;

import java.time.LocalDateTime;

/**
 * DTO für Medication (Record)
 * Medikamentenverwaltung pro Patient.
 * Immutable und kompakt mit Java Records.
 */
public record MedicationDTO(
    Long id,
    Long patientId,
    String name,
    String dosage,
    String intakeTime,
    Boolean confirmable,
    LocalDateTime createdAt
) {
}

