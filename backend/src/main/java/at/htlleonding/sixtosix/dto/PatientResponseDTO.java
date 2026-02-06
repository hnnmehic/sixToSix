package at.htlleonding.sixtosix.dto;

import java.time.LocalDate;

/**
 * DTO für Patient-Response (Record)
 * Wird für GET Requests verwendet (ohne sensitive Daten).
 * Immutable und kompakt mit Java Records.
 */
public record PatientResponseDTO(
    Long id,
    String firstname,
    String lastname,
    LocalDate birthdate,
    Boolean deleted
) {
}

