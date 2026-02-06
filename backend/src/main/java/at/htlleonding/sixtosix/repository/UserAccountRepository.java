package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.UserAccount;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

/**
 * Repository for UserAccount Entity
 * Handles CRUD and complex queries for Users.
 */
@ApplicationScoped
public class UserAccountRepository implements PanacheRepository<UserAccount> {

    /**
     * Find user by Keycloak ID
     */
    public Optional<UserAccount> findByKeycloakId(String keycloakId) {
        return find("keycloakId", keycloakId).firstResultOptional();
    }

    /**
     * Check if user exists by Keycloak ID
     */
    public boolean existsByKeycloakId(String keycloakId) {
        return find("keycloakId", keycloakId).count() > 0;
    }
}

