package at.htlleonding.sixtosix.resource;

import at.htlleonding.sixtosix.dto.ADLAssessmentDTO;
import at.htlleonding.sixtosix.dto.ADLDefinitionDTO;
import at.htlleonding.sixtosix.entity.*;
import at.htlleonding.sixtosix.repository.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST Resource für ADL-Operationen (Aktivitäten des täglichen Lebens)
 * Verwaltung von ADL-Definitionen und ADL-Bewertungen
 *
 * Basierend auf FSD Abschnitt 5 (ADLs und Skills)
 * und TSD Abschnitt 5.4 (adl_definition und adl_assessment Tabellen)
 *
 * Endpunkte:
 * - GET    /adl-definitions                      - Alle vordefinierte ADLs
 * - POST   /patients/{patientId}/adl-assessments - Neue ADL-Bewertung
 * - GET    /patients/{patientId}/adl-assessments - ADL-Bewertungen eines Patienten
 * - GET    /adl-assessments/{id}                 - ADL-Bewertung-Details
 */
@Path("/adl")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ADLAssessmentResource {

    @Inject
    ADLDefinitionRepository adlDefinitionRepository;

    @Inject
    ADLAssessmentRepository adlAssessmentRepository;

    @Inject
    PatientRepository patientRepository;

    @Inject
    UserAccountRepository userAccountRepository;

    // ==================== ADL Definitions ====================

    /**
     * GET /adl-definitions
     * Gibt alle vordefinierten ADLs zurück.
     *
     * Basierend auf FSD Abschnitt 5.1 (ADLs sind vordefiniert)
     *
     * @return Liste aller ADL-Definitionen
     */
    @GET
    @Path("/definitions")
    public List<ADLDefinitionDTO> getAllADLDefinitions() {
        return adlDefinitionRepository.listAll()
                .stream()
                .map(this::mapDefinitionToDTO)
                .toList();
    }

    /**
     * POST /adl-definitions
     * Erstellt eine neue ADL-Definition (Admin-Funktion).
     *
     * Request Body:
     * {
     *   "name": "Essen"
     * }
     *
     * @param name ADL-Name
     * @return 201 Created mit neuer Definition
     */
    @POST
    @Path("/definitions")
    public Response createADLDefinition(@QueryParam("name") String name) {
        if (name == null || name.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ADL-Name darf nicht leer sein")
                    .build();
        }

        // Prüfe ob ADL bereits existiert
        if (adlDefinitionRepository.existsByName(name)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("ADL-Definition existiert bereits")
                    .build();
        }

        ADLDefinition definition = new ADLDefinition(name);
        adlDefinitionRepository.persist(definition);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapDefinitionToDTO(definition))
                .build();
    }

    // ==================== ADL Assessments ====================

    /**
     * POST /patients/{patientId}/adl-assessments
     * Erstellt eine neue ADL-Bewertung für einen Patienten.
     *
     * Request Body:
     * {
     *   "adlDefinitionId": 1,
     *   "status": "NURSING_RELEVANT",
     *   "assessedById": 5
     * }
     *
     * Status-Werte: INTACT, RESTRICTED, NURSING_RELEVANT
     *
     * Basierend auf FSD Abschnitt 5.2 (ADLs können mehrfach neu eingeschätzt werden)
     *
     * @param patientId Patient-ID
     * @param adlDefinitionId ADL-Definition-ID
     * @param status Bewertungs-Status
     * @param assessedById ID des Bewerters
     * @return 201 Created mit neuer Bewertung
     */
    @POST
    @Path("/patients/{patientId}/assessments")
    public Response createADLAssessment(
            @PathParam("patientId") Long patientId,
            @QueryParam("adlDefinitionId") Long adlDefinitionId,
            @QueryParam("status") String status,
            @QueryParam("assessedById") Long assessedById) {

        // Validiere Patient
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        // Validiere ADL-Definition
        ADLDefinition adlDefinition = adlDefinitionRepository.findById(adlDefinitionId);
        if (adlDefinition == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ADL-Definition nicht gefunden")
                    .build();
        }

        // Validiere Status
        AssessmentStatus assessmentStatus;
        try {
            assessmentStatus = AssessmentStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ungültiger Status: " + status)
                    .build();
        }

        // Validiere Bewerter
        UserAccount assessedBy = userAccountRepository.findById(assessedById);
        if (assessedBy == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Bewerter nicht gefunden")
                    .build();
        }

        // Erstelle neue Bewertung
        ADLAssessment assessment = new ADLAssessment(adlDefinition, patient, assessmentStatus, assessedBy);
        adlAssessmentRepository.persist(assessment);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapAssessmentToDTO(assessment))
                .build();
    }

    /**
     * GET /patients/{patientId}/adl-assessments
     * Gibt alle ADL-Bewertungen eines Patienten zurück.
     *
     * Basierend auf FSD Abschnitt 5.2 (Der zeitliche Verlauf ist nachvollziehbar)
     *
     * @param patientId Patient-ID
     * @return Liste aller Bewertungen für den Patienten
     */
    @GET
    @Path("/patients/{patientId}/assessments")
    public Response getADLAssessmentsForPatient(@PathParam("patientId") Long patientId) {
        // Validiere Patient
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        List<ADLAssessmentDTO> assessments = adlAssessmentRepository
                .findByPatient(patient)
                .stream()
                .map(this::mapAssessmentToDTO)
                .toList();

        return Response.ok(assessments).build();
    }

    /**
     * GET /adl-assessments/{id}
     * Gibt eine einzelne ADL-Bewertung zurück.
     *
     * @param id ADLAssessment-ID
     * @return Bewertungs-Details
     */
    @GET
    @Path("/assessments/{id}")
    public Response getADLAssessment(@PathParam("id") Long id) {
        ADLAssessment assessment = adlAssessmentRepository.findById(id);

        if (assessment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("ADL-Bewertung nicht gefunden")
                    .build();
        }

        return Response.ok(mapAssessmentToDTO(assessment)).build();
    }

    /**
     * GET /patients/{patientId}/adl-assessments/latest
     * Gibt die neuesten ADL-Bewertungen pro ADL für einen Patienten zurück.
     *
     * Basierend auf FSD Abschnitt 5.3 (Pflegerelevante ADLs werden automatisch vorgeschlagen)
     *
     * @param patientId Patient-ID
     * @return Neueste Bewertungen pro ADL
     */
    @GET
    @Path("/patients/{patientId}/assessments/latest")
    public Response getLatestADLAssessments(@PathParam("patientId") Long patientId) {
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        List<ADLAssessmentDTO> latestAssessments = adlAssessmentRepository
                .findLatestByPatient(patient)
                .stream()
                .map(this::mapAssessmentToDTO)
                .toList();

        return Response.ok(latestAssessments).build();
    }

    // ==================== Mapping Methods ====================

    /**
     * Mappt ADLDefinition Entity zu ADLDefinitionDTO
     */
    private ADLDefinitionDTO mapDefinitionToDTO(ADLDefinition definition) {
        return new ADLDefinitionDTO(
                definition.getId(),
                definition.getName()
        );
    }

    /**
     * Mappt ADLAssessment Entity zu ADLAssessmentDTO
     */
    private ADLAssessmentDTO mapAssessmentToDTO(ADLAssessment assessment) {
        return new ADLAssessmentDTO(
                assessment.getId(),
                assessment.getAdlDefinition().getId(),
                assessment.getAdlDefinition().getName(),
                assessment.getPatient().getId(),
                assessment.getStatus().toString(),
                assessment.getAssessedAt(),
                assessment.getAssessedBy().getId(),
                assessment.getAssessedBy().getKeycloakId()
        );
    }
}



