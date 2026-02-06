package at.htlleonding.sixtosix.resource;

import at.htlleonding.sixtosix.dto.ResourceDTO;
import at.htlleonding.sixtosix.entity.Patient;
import at.htlleonding.sixtosix.entity.Resource;
import at.htlleonding.sixtosix.entity.UserAccount;
import at.htlleonding.sixtosix.repository.PatientRepository;
import at.htlleonding.sixtosix.repository.ResourceRepository;
import at.htlleonding.sixtosix.repository.UserAccountRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST Resource für Resource-Operationen
 * Verwaltung sozialer, persönlicher und biografischer Faktoren
 *
 * Basierend auf FSD Abschnitt 6 (Ressourcen)
 * und TSD Abschnitt 5.6 (resource Tabelle)
 *
 * Endpunkte:
 * - POST   /patients/{patientId}/resources              - Neue Ressource hinzufügen
 * - GET    /patients/{patientId}/resources              - Alle Ressourcen eines Patienten
 * - GET    /patients/{patientId}/resources/approved     - Bestätigte Ressourcen
 * - GET    /patients/{patientId}/resources/pending      - Ausstehende Ressourcen
 * - PUT    /resources/{id}/approve                     - Ressource bestätigen
 * - DELETE /resources/{id}                             - Ressource löschen
 */
@Path("/resources")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResourceResource {

    @Inject
    ResourceRepository resourceRepository;

    @Inject
    PatientRepository patientRepository;

    @Inject
    UserAccountRepository userAccountRepository;

    /**
     * POST /patients/{patientId}/resources
     * Fügt eine neue Ressource für einen Patienten hinzu.
     *
     * Query Parameter:
     * - type: Typ der Ressource (z.B. Familie, Freunde, Beruf)
     * - description: Beschreibung
     * - proposedById: ID des Vorschlagenden (optional)
     *
     * Basierend auf FSD Abschnitt 6 (Ressourcen umfassen Familie, Freunde, etc.)
     * und FSD Abschnitt 6.3 (Patienten können Ressourcen vorschlagen)
     *
     * @param patientId Patient-ID
     * @param type Ressourcentyp
     * @param description Beschreibung
     * @param proposedById ID des Vorschlagenden
     * @return 201 Created mit neuer Ressource
     */
    @POST
    @Path("/patients/{patientId}")
    public Response createResource(
            @PathParam("patientId") Long patientId,
            @QueryParam("type") String type,
            @QueryParam("description") String description,
            @QueryParam("proposedById") Long proposedById) {

        // Validiere Patient
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        // Validiere Type
        if (type == null || type.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Typ darf nicht leer sein")
                    .build();
        }

        // Validiere Description
        if (description == null || description.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Beschreibung darf nicht leer sein")
                    .build();
        }

        // Erstelle neue Ressource
        Resource resource = new Resource(patient, type, description);

        // Setze Proposer falls vorhanden
        if (proposedById != null) {
            UserAccount proposer = userAccountRepository.findById(proposedById);
            if (proposer != null) {
                resource.setProposedBy(proposer);
            }
        }

        resourceRepository.persist(resource);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapToDTO(resource))
                .build();
    }

    /**
     * GET /patients/{patientId}/resources
     * Gibt alle Ressourcen eines Patienten zurück.
     *
     * Basierend auf FSD Abschnitt 6.2 (Ressourcen werden aktiv in Interventionen genutzt)
     *
     * @param patientId Patient-ID
     * @return Liste aller Ressourcen
     */
    @GET
    @Path("/patients/{patientId}")
    public Response getResourcesForPatient(@PathParam("patientId") Long patientId) {
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        List<ResourceDTO> resources = resourceRepository
                .findByPatient(patient)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return Response.ok(resources).build();
    }

    /**
     * GET /patients/{patientId}/resources/approved
     * Gibt alle bestätigten Ressourcen eines Patienten zurück.
     *
     * @param patientId Patient-ID
     * @return Liste bestätigter Ressourcen
     */
    @GET
    @Path("/patients/{patientId}/approved")
    public Response getApprovedResourcesForPatient(@PathParam("patientId") Long patientId) {
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        List<ResourceDTO> resources = resourceRepository
                .findApprovedByPatient(patient)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return Response.ok(resources).build();
    }

    /**
     * GET /patients/{patientId}/resources/pending
     * Gibt alle ausstehenden (nicht bestätigten) Ressourcen zurück.
     *
     * Basierend auf FSD Abschnitt 6.3 (Pfleger prüfen und bestätigen vorgeschlagene Ressourcen)
     *
     * @param patientId Patient-ID
     * @return Liste ausstehender Ressourcen
     */
    @GET
    @Path("/patients/{patientId}/pending")
    public Response getPendingResourcesForPatient(@PathParam("patientId") Long patientId) {
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        List<ResourceDTO> resources = resourceRepository
                .findPendingByPatient(patient)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return Response.ok(resources).build();
    }

    /**
     * PUT /resources/{id}/approve
     * Bestätigt eine Ressource.
     *
     * Basierend auf FSD Abschnitt 6.3 (Pfleger prüfen und bestätigen)
     *
     * @param id Resource-ID
     * @return Bestätigte Ressource
     */
    @PUT
    @Path("/{id}/approve")
    public Response approveResource(@PathParam("id") Long id) {
        Resource resource = resourceRepository.findById(id);

        if (resource == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Ressource nicht gefunden")
                    .build();
        }

        resource.setApproved(true);
        resourceRepository.persist(resource);

        return Response.ok(mapToDTO(resource)).build();
    }

    /**
     * DELETE /resources/{id}
     * Löscht eine Ressource.
     *
     * @param id Resource-ID
     * @return 204 No Content
     */
    @DELETE
    @Path("/{id}")
    public Response deleteResource(@PathParam("id") Long id) {
        Resource resource = resourceRepository.findById(id);

        if (resource == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Ressource nicht gefunden")
                    .build();
        }

        resourceRepository.delete(resource);

        return Response.noContent().build();
    }

    // ==================== Mapping Methods ====================

    /**
     * Mappt Resource Entity zu ResourceDTO
     */
    private ResourceDTO mapToDTO(Resource resource) {
        return new ResourceDTO(
                resource.getId(),
                resource.getPatient().getId(),
                resource.getType(),
                resource.getDescription(),
                resource.getProposedBy() != null ? resource.getProposedBy().getId() : null,
                resource.getProposedBy() != null ? resource.getProposedBy().getKeycloakId() : null,
                resource.getApproved(),
                resource.getCreatedAt()
        );
    }
}



