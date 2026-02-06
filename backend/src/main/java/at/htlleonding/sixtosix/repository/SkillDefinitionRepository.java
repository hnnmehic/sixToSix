package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.SkillDefinition;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

/**
 * Repository for SkillDefinition Entity
 * Handles CRUD and queries for Skill Definitions.
 */
@ApplicationScoped
public class SkillDefinitionRepository implements PanacheRepository<SkillDefinition> {

    /**
     * Find Skill by name
     */
    public Optional<SkillDefinition> findByName(String name) {
        return find("name", name).firstResultOptional();
    }

    /**
     * Check if Skill exists by name
     */
    public boolean existsByName(String name) {
        return find("name", name).count() > 0;
    }
}

