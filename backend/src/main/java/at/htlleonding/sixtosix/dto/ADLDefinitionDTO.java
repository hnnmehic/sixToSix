package at.htlleonding.sixtosix.dto;

/**
 * DTO für ADLDefinition (Record)
 * Vordefinierte Aktivitäten des täglichen Lebens.
 * Immutable und kompakt mit Java Records.
 */
public record ADLDefinitionDTO(
    Long id,
    String name
) {
}

