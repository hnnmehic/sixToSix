package at.htlleonding.sixtosix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

/**
 * DTO für Patient-Erstellung und Aktualisierung (Record)
 * Wird für POST/PUT Requests verwendet.
 * Immutable und kompakt mit Java Records.
 */
public record PatientCreateUpdateDTO(
    @NotBlank(message = "Vorname darf nicht leer sein")
    String firstname,

    @NotBlank(message = "Nachname darf nicht leer sein")
    String lastname,

    @NotNull(message = "Geburtsdatum darf nicht null sein")
    @Past(message = "Geburtsdatum muss in der Vergangenheit liegen")
    LocalDate birthdate
) {
}

