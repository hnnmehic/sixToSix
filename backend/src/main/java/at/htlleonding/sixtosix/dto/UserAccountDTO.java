package at.htlleonding.sixtosix.dto;

/**
 * DTO für UserAccount (Record)
 * Benutzer/Pfleger-Informationen.
 * Immutable und kompakt mit Java Records.
 */
public record UserAccountDTO(
    Long id,
    String keycloakId,
    String role
) {
}

