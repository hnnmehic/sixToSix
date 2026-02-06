package at.htlleonding.sixtosix.resource;

import at.htlleonding.sixtosix.dto.UserAccountDTO;
import at.htlleonding.sixtosix.entity.UserAccount;
import at.htlleonding.sixtosix.entity.UserRole;
import at.htlleonding.sixtosix.repository.UserAccountRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST Resource für UserAccount-Operationen (Betreuer/Pfleger)
 * Basierend auf TSD Abschnitt 7 (REST API) und FSD Abschnitt 3 (Rollen und Rechte)
 *
 * Endpunkte:
 * - GET    /users                      - Alle Benutzer
 * - GET    /users/{id}                 - Benutzer-Details
 * - GET    /users/keycloak/{keycloakId} - Benutzer via Keycloak-ID suchen
 * - POST   /users/sync                 - Neuen Benutzer aus Keycloak synchen
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserAccountResource {

    @Inject
    UserAccountRepository userAccountRepository;

    /**
     * GET /users
     * Gibt alle Benutzer zurück.
     *
     * Basierend auf FSD Abschnitt 3.1 (Pfleger-Verwaltung)
     *
     * @return Liste aller Benutzer
     */
    @GET
    public List<UserAccountDTO> getAllUsers() {
        return userAccountRepository.listAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * GET /users/{id}
     * Gibt einen einzelnen Benutzer zurück.
     *
     * @param id User-ID
     * @return Benutzer-Daten
     */
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        UserAccount user = userAccountRepository.findById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Benutzer nicht gefunden")
                    .build();
        }

        return Response.ok(mapToDTO(user)).build();
    }

    /**
     * GET /users/keycloak/{keycloakId}
     * Sucht einen Benutzer via Keycloak-ID.
     *
     * Basierend auf TSD Abschnitt 4.1 (Authentifizierung via Keycloak)
     *
     * @param keycloakId Keycloak-ID
     * @return Benutzer-Daten oder 404
     */
    @GET
    @Path("/keycloak/{keycloakId}")
    public Response getUserByKeycloakId(@PathParam("keycloakId") String keycloakId) {
        var user = userAccountRepository.findByKeycloakId(keycloakId);

        if (user.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Benutzer mit Keycloak-ID nicht gefunden")
                    .build();
        }

        return Response.ok(mapToDTO(user.get())).build();
    }

    /**
     * POST /users/sync
     * Syncht einen neuen Benutzer aus Keycloak.
     *
     * Wird aufgerufen, wenn sich ein Benutzer zum ersten Mal anmeldet.
     *
     * Request Body:
     * {
     *   "keycloakId": "uuid",
     *   "role": "PFLEGER"
     * }
     *
     * Basierend auf FSD Abschnitt 3.1 (Pfleger-Verwaltung)
     *
     * @param keycloakId Keycloak-ID
     * @param role Rolle (PFLEGER oder PATIENT)
     * @return 201 Created mit neuem Benutzer
     */
    @POST
    @Path("/sync")
    public Response syncUserFromKeycloak(
            @QueryParam("keycloakId") String keycloakId,
            @QueryParam("role") String role) {

        // Prüfe ob Benutzer bereits existiert
        if (userAccountRepository.existsByKeycloakId(keycloakId)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Benutzer existiert bereits")
                    .build();
        }

        // Validiere Role
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(role);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ungültige Rolle: " + role)
                    .build();
        }

        // Erstelle neuen Benutzer
        UserAccount newUser = new UserAccount(keycloakId, userRole);
        userAccountRepository.persist(newUser);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapToDTO(newUser))
                .build();
    }

    /**
     * GET /users/{id}/patients
     * Gibt alle Patienten eines Pflegers zurück.
     *
     * Basierend auf FSD Abschnitt 3.1 (Pfleger verwaltet Patienten)
     *
     * @param id User-ID
     * @return Liste der Patienten-IDs
     */
    @GET
    @Path("/{id}/patients")
    public Response getPatientsForUser(@PathParam("id") Long id) {
        UserAccount user = userAccountRepository.findById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Benutzer nicht gefunden")
                    .build();
        }

        // Mappt Patienten über CareAssignments
        List<Long> patientIds = user.getCareAssignments()
                .stream()
                .filter(assignment -> assignment.getActive())
                .map(assignment -> assignment.getPatient().getId())
                .toList();

        return Response.ok(patientIds).build();
    }

    // ==================== Mapping Methods ====================

    /**
     * Mappt UserAccount Entity zu UserAccountDTO
     */
    private UserAccountDTO mapToDTO(UserAccount user) {
        return new UserAccountDTO(
                user.getId(),
                user.getKeycloakId(),
                user.getRole().toString()
        );
    }
}


