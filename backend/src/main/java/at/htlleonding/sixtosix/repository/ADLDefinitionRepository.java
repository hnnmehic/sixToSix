package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.ADLDefinition;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

/**
 * Repository for ADLDefinition Entity
 * Handles CRUD and queries for ADL Definitions.
 */
@ApplicationScoped
public class ADLDefinitionRepository implements PanacheRepository<ADLDefinition> {

    /**
     * Find ADL by name
     */
    public Optional<ADLDefinition> findByName(String name) {
        return find("name", name).firstResultOptional();
    }

    /**
     * Check if ADL exists by name
     */
    public boolean existsByName(String name) {
        return find("name", name).count() > 0;
    }
}

