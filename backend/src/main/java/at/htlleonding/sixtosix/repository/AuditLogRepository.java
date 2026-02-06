package at.htlleonding.sixtosix.repository;

import at.htlleonding.sixtosix.entity.AuditLog;
import at.htlleonding.sixtosix.entity.AuditAction;
import at.htlleonding.sixtosix.entity.UserAccount;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for AuditLog Entity
 * Handles queries for Audit Logs (immutable records).
 */
@ApplicationScoped
public class AuditLogRepository implements PanacheRepository<AuditLog> {

    /**
     * Find all audit logs for an entity
     */
    public List<AuditLog> findByEntity(String entity, Long entityId) {
        return find("entity = ?1 and entityId = ?2 order by performedAt desc", entity, entityId).list();
    }

    /**
     * Find audit logs by action
     */
    public List<AuditLog> findByAction(AuditAction action) {
        return find("action = ?1 order by performedAt desc", action).list();
    }

    /**
     * Find audit logs by performer
     */
    public List<AuditLog> findByPerformer(UserAccount performer) {
        return find("performedBy = ?1 order by performedAt desc", performer).list();
    }

    /**
     * Find audit logs within date range
     */
    public List<AuditLog> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return find("performedAt between ?1 and ?2 order by performedAt desc", startDate, endDate).list();
    }

    /**
     * Find audit logs by entity and date range
     */
    public List<AuditLog> findByEntityAndDateRange(String entity, Long entityId, LocalDateTime startDate, LocalDateTime endDate) {
        return find("entity = ?1 and entityId = ?2 and performedAt between ?3 and ?4 order by performedAt desc",
                entity, entityId, startDate, endDate).list();
    }
}

