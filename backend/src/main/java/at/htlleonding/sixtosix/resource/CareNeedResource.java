package at.htlleonding.sixtosix.resource;

import at.htlleonding.sixtosix.dto.CareNeedDTO;
import at.htlleonding.sixtosix.entity.CareNeed;
import at.htlleonding.sixtosix.entity.Patient;
import at.htlleonding.sixtosix.repository.CareNeedRepository;
import at.htlleonding.sixtosix.repository.PatientRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST Resource für CareNeed-Operationen
 * Verwaltung von ad-hoc erfassten Bedarf- und Krisensituationen
 *
 * Basierend auf FSD Abschnitt 10 (Bedarf und Krisensituationen)
 * und TSD Abschnitt 5.10 (care_need Tabelle)
 *
 * Endpunkte:
 * - POST   /patients/{patientId}/care-needs              - Neuer Bedarf erfassen
 * - GET    /patients/{patientId}/care-needs              - Alle Bedarfe eines Patienten
 * - GET    /patients/{patientId}/care-needs/active       - Aktive Bedarfe
 * - GET    /patients/{patientId}/care-needs/resolved     - Gelöste Bedarfe
 * - PUT    /care-needs/{id}/resolve                     - Bedarf als gelöst markieren
 * - DELETE /care-needs/{id}                             - Bedarf löschen
 */
@Path("/care-needs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CareNeedResource {

    @Inject
    CareNeedRepository careNeedRepository;

    @Inject
    PatientRepository patientRepository;

    /**
     * POST /patients/{patientId}/care-needs
     * Erfasst einen neuen Bedarf/Krise für einen Patienten.
     *
     * Query Parameter:
     * - description: Beschreibung des Bedarfs
     *
     * Basierend auf FSD Abschnitt 10 (Bedarf kann ad-hoc erfasst werden)
     * Beispiel: Blutdruck > 160/90
     *
     * @param patientId Patient-ID
     * @param description Beschreibung
     * @return 201 Created mit neuem Bedarf
     */
    @POST
    @Path("/patients/{patientId}")
    public Response createCareNeed(
            @PathParam("patientId") Long patientId,
            @QueryParam("description") String description) {

        // Validiere Patient
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        // Validiere Description
        if (description == null || description.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Beschreibung darf nicht leer sein")
                    .build();
        }

        // Erstelle neuen Bedarf
        CareNeed careNeed = new CareNeed(patient, description);
        careNeedRepository.persist(careNeed);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapToDTO(careNeed))
                .build();
    }

    /**
     * GET /patients/{patientId}/care-needs
     * Gibt alle Bedarfe eines Patienten zurück.
     *
     * Basierend auf FSD Abschnitt 10 (Bedarf bleibt aktiv bis beendet)
     * und FSD Abschnitt 10 (Bedarf dient der Dokumentation und Nachvollziehbarkeit)
     *
     * @param patientId Patient-ID
     * @return Liste aller Bedarfe
     */
    @GET
    @Path("/patients/{patientId}")
    public Response getCareNeedsForPatient(@PathParam("patientId") Long patientId) {
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        List<CareNeedDTO> careNeeds = careNeedRepository
                .findByPatient(patient)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return Response.ok(careNeeds).build();
    }

    /**
     * GET /patients/{patientId}/care-needs/active
     * Gibt alle aktiven Bedarfe eines Patienten zurück.
     *
     * Basierend auf FSD Abschnitt 10 (Bedarf bleibt aktiv)
     *
     * @param patientId Patient-ID
     * @return Liste aktiver Bedarfe
     */
    @GET
    @Path("/patients/{patientId}/active")
    public Response getActiveCareNeedsForPatient(@PathParam("patientId") Long patientId) {
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        List<CareNeedDTO> careNeeds = careNeedRepository
                .findActiveByPatient(patient)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return Response.ok(careNeeds).build();
    }

    /**
     * GET /patients/{patientId}/care-needs/resolved
     * Gibt alle gelösten Bedarfe eines Patienten zurück.
     *
     * @param patientId Patient-ID
     * @return Liste gelöster Bedarfe
     */
    @GET
    @Path("/patients/{patientId}/resolved")
    public Response getResolvedCareNeedsForPatient(@PathParam("patientId") Long patientId) {
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        List<CareNeedDTO> careNeeds = careNeedRepository
                .findResolvedByPatient(patient)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return Response.ok(careNeeds).build();
    }

    /**
     * PUT /care-needs/{id}/resolve
     * Markiert einen Bedarf als gelöst.
     *
     * Basierend auf FSD Abschnitt 10 (Bedarf bleibt aktiv bis er beendet wird)
     *
     * @param id CareNeed-ID
     * @return Gelöster Bedarf
     */
    @PUT
    @Path("/{id}/resolve")
    public Response resolveCareNeed(@PathParam("id") Long id) {
        CareNeed careNeed = careNeedRepository.findById(id);

        if (careNeed == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Bedarf nicht gefunden")
                    .build();
        }

        if (!careNeed.getActive()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Bedarf ist bereits gelöst")
                    .build();
        }

        careNeed.resolve();
        careNeedRepository.persist(careNeed);

        return Response.ok(mapToDTO(careNeed)).build();
    }

    /**
     * DELETE /care-needs/{id}
     * Löscht einen Bedarf.
     *
     * @param id CareNeed-ID
     * @return 204 No Content
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCareNeed(@PathParam("id") Long id) {
        CareNeed careNeed = careNeedRepository.findById(id);

        if (careNeed == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Bedarf nicht gefunden")
                    .build();
        }

        careNeedRepository.delete(careNeed);

        return Response.noContent().build();
    }

    // ==================== Mapping Methods ====================

    /**
     * Mappt CareNeed Entity zu CareNeedDTO
     */
    private CareNeedDTO mapToDTO(CareNeed careNeed) {
        return new CareNeedDTO(
                careNeed.getId(),
                careNeed.getPatient().getId(),
                careNeed.getDescription(),
                careNeed.getActive(),
                careNeed.getCreatedAt(),
                careNeed.getResolvedAt()
        );
    }
}



