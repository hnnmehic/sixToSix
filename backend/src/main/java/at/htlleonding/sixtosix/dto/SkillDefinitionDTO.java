package at.htlleonding.sixtosix.dto;

/**
 * DTO für SkillDefinition (Record)
 * Vordefinierte Fähigkeitsbereiche.
 * Immutable und kompakt mit Java Records.
 */
public record SkillDefinitionDTO(
    Long id,
    String name
) {
}

