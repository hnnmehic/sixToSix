package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.Anamnesis;
import at.htlleonding.sixtosix.entity.AnamnesisVersion;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

/**
 * Repository for AnamnesisVersion Entity
 * Handles CRUD and versioning queries for Anamnesis.
 */
@ApplicationScoped
public class AnamnesisVersionRepository implements PanacheRepository<AnamnesisVersion> {

    /**
     * Find all versions of an anamnesis ordered by version number
     */
    public List<AnamnesisVersion> findVersionsByAnamnesis(Anamnesis anamnesis) {
        return find("anamnesis = ?1 order by versionNumber asc", anamnesis).list();
    }

    /**
     * Find specific version by number
     */
    public Optional<AnamnesisVersion> findByAnamnesisAndVersion(Anamnesis anamnesis, Long versionNumber) {
        return find("anamnesis = ?1 and versionNumber = ?2", anamnesis, versionNumber).firstResultOptional();
    }

    /**
     * Find latest version
     */
    public Optional<AnamnesisVersion> findLatestVersion(Anamnesis anamnesis) {
        return find("anamnesis = ?1 order by versionNumber desc", anamnesis)
                .firstResultOptional();
    }

    /**
     * Find all finalized versions
     */
    public List<AnamnesisVersion> findFinalizedVersions(Anamnesis anamnesis) {
        return find("anamnesis = ?1 and finalized = true order by versionNumber asc", anamnesis).list();
    }

    /**
     * Get next version number
     */
    public Long getNextVersionNumber(Anamnesis anamnesis) {
        Optional<AnamnesisVersion> latest = findLatestVersion(anamnesis);
        return latest.map(v -> v.getVersionNumber() + 1).orElse(1L);
    }
}

