package at.htlleonding.sixtosix.resource;

import at.htlleonding.sixtosix.dto.CareAssignmentDTO;
import at.htlleonding.sixtosix.entity.CareAssignment;
import at.htlleonding.sixtosix.entity.Patient;
import at.htlleonding.sixtosix.entity.UserAccount;
import at.htlleonding.sixtosix.repository.CareAssignmentRepository;
import at.htlleonding.sixtosix.repository.PatientRepository;
import at.htlleonding.sixtosix.repository.UserAccountRepository;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST Resource für CareAssignment-Operationen
 * Verwaltung der Zuordnung zwischen Pfleger und Patient
 *
 * Basierend auf FSD Abschnitt 3 (Rollen und Rechte)
 * und TSD Abschnitt 5.2 (care_assignment Tabelle)
 *
 * Endpunkte:
 * - GET    /care-assignments              - Alle Zuordnungen
 * - POST   /care-assignments              - Neue Zuordnung erstellen
 * - GET    /care-assignments/{id}         - Zuordnung-Details
 * - PUT    /care-assignments/{id}         - Zuordnung aktualisieren
 * - DELETE /care-assignments/{id}         - Zuordnung deaktivieren
 */
@Path("/care-assignments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CareAssignmentResource {

    @Inject
    CareAssignmentRepository careAssignmentRepository;

    @Inject
    UserAccountRepository userAccountRepository;

    @Inject
    PatientRepository patientRepository;

    /**
     * GET /care-assignments
     * Gibt alle Zuordnungen zurück.
     *
     * @return Liste aller Care Assignments
     */
    @GET
    public List<CareAssignmentDTO> getAllAssignments() {
        return careAssignmentRepository.listAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * POST /care-assignments
     * Erstellt eine neue Zuordnung zwischen Pfleger und Patient.
     *
     * Request Body:
     * {
     *   "pflegerUserId": 1,
     *   "patientId": 2
     * }
     *
     * Basierend auf FSD Abschnitt 3 (Rechtesystem)
     *
     * @param assignmentDTO Zuordnung-Daten
     * @return 201 Created mit neuer Zuordnung
     */
    @POST
    public Response createAssignment(CareAssignmentDTO assignmentDTO) {
        // Validiere Pfleger
        UserAccount pfleger = userAccountRepository.findById(assignmentDTO.pflegerUserId());
        if (pfleger == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Pfleger nicht gefunden")
                    .build();
        }

        // Validiere Patient
        Patient patient = patientRepository.findById(assignmentDTO.patientId());
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Patient nicht gefunden oder gelöscht")
                    .build();
        }

        // Prüfe ob Zuordnung bereits existiert
        var existingAssignment = careAssignmentRepository.findByPflegerAndPatient(pfleger, patient);
        if (existingAssignment.isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Zuordnung existiert bereits")
                    .build();
        }

        // Erstelle neue Zuordnung
        CareAssignment assignment = new CareAssignment(pfleger, patient);
        careAssignmentRepository.persist(assignment);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapToDTO(assignment))
                .build();
    }

    /**
     * GET /care-assignments/{id}
     * Gibt eine einzelne Zuordnung zurück.
     *
     * @param id CareAssignment-ID
     * @return Zuordnung-Details
     */
    @GET
    @Path("/{id}")
    public Response getAssignment(@PathParam("id") Long id) {
        CareAssignment assignment = careAssignmentRepository.findById(id);

        if (assignment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Zuordnung nicht gefunden")
                    .build();
        }

        return Response.ok(mapToDTO(assignment)).build();
    }

    /**
     * PUT /care-assignments/{id}
     * Aktualisiert eine Zuordnung (z.B. Deaktivieren).
     *
     * Request Body:
     * {
     *   "active": false
     * }
     *
     * @param id CareAssignment-ID
     * @param updateDTO Update-Daten
     * @return Aktualisierte Zuordnung
     */
    @PUT
    @Path("/{id}")
    public Response updateAssignment(@PathParam("id") Long id, CareAssignmentDTO updateDTO) {
        CareAssignment assignment = careAssignmentRepository.findById(id);

        if (assignment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Zuordnung nicht gefunden")
                    .build();
        }

        // Nur active-Flag kann geändert werden
        if (updateDTO.active() != null) {
            assignment.setActive(updateDTO.active());
            careAssignmentRepository.persist(assignment);
        }

        return Response.ok(mapToDTO(assignment)).build();
    }

    /**
     * DELETE /care-assignments/{id}
     * Deaktiviert eine Zuordnung (nicht wirklich löschen, sondern deaktivieren).
     *
     * Basierend auf FSD Abschnitt 3 (Rechte können nachträglich geändert werden)
     *
     * @param id CareAssignment-ID
     * @return 204 No Content
     */
    @DELETE
    @Path("/{id}")
    public Response deleteAssignment(@PathParam("id") Long id) {
        CareAssignment assignment = careAssignmentRepository.findById(id);

        if (assignment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Zuordnung nicht gefunden")
                    .build();
        }

        careAssignmentRepository.deactivateAssignment(id);

        return Response.noContent().build();
    }

    /**
     * GET /care-assignments/pfleger/{pflegerId}/active
     * Gibt alle aktiven Zuordnungen eines Pflegers zurück.
     *
     * @param pflegerId User-ID des Pflegers
     * @return Liste aktiver Zuordnungen
     */
    @GET
    @Path("/pfleger/{pflegerId}/active")
    public Response getActiveAssignmentsForPfleger(@PathParam("pflegerId") Long pflegerId) {
        UserAccount pfleger = userAccountRepository.findById(pflegerId);

        if (pfleger == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Pfleger nicht gefunden")
                    .build();
        }

        List<CareAssignmentDTO> assignments = careAssignmentRepository
                .findActiveByPfleger(pfleger)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return Response.ok(assignments).build();
    }

    /**
     * GET /care-assignments/patient/{patientId}/active
     * Gibt alle aktiven Pfleger eines Patienten zurück.
     *
     * @param patientId Patient-ID
     * @return Liste aktiver Zuordnungen
     */
    @GET
    @Path("/patient/{patientId}/active")
    public Response getActiveAssignmentsForPatient(@PathParam("patientId") Long patientId) {
        Patient patient = patientRepository.findById(patientId);

        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        List<CareAssignmentDTO> assignments = careAssignmentRepository
                .findActiveByPatient(patient)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return Response.ok(assignments).build();
    }

    // ==================== Mapping Methods ====================

    /**
     * Mappt CareAssignment Entity zu CareAssignmentDTO
     */
    private CareAssignmentDTO mapToDTO(CareAssignment assignment) {
        return new CareAssignmentDTO(
                assignment.getId(),
                assignment.getPfleger().getId(),
                assignment.getPfleger().getKeycloakId(), // Vereinfachung: zeige Keycloak-ID
                assignment.getPatient().getId(),
                assignment.getPatient().getFirstname() + " " + assignment.getPatient().getLastname(),
                assignment.getActive(),
                assignment.getCreatedAt()
        );
    }
}



