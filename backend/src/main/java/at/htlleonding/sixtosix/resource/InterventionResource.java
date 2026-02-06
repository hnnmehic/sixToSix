package at.htlleonding.sixtosix.resource;

import at.htlleonding.sixtosix.dto.InterventionDTO;
import at.htlleonding.sixtosix.dto.InterventionTaskDTO;
import at.htlleonding.sixtosix.entity.*;
import at.htlleonding.sixtosix.repository.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST Resource für Intervention-Operationen
 * Verwaltung von Pflegemaßnahmen und deren Aufgaben
 *
 * Basierend auf FSD Abschnitt 7 (Interventionen)
 * und TSD Abschnitt 5.7 (intervention und intervention_task Tabellen)
 *
 * Endpunkte:
 * - POST   /patients/{patientId}/interventions           - Neue Intervention
 * - GET    /patients/{patientId}/interventions           - Interventionen eines Patienten
 * - GET    /interventions/{id}                          - Intervention-Details
 * - PUT    /interventions/{id}                          - Intervention aktualisieren
 * - DELETE /interventions/{id}                          - Intervention deaktivieren
 * - POST   /interventions/{interventionId}/tasks        - Task zur Intervention hinzufügen
 * - PUT    /intervention-tasks/{taskId}/complete        - Task markieren als erledigt
 */
@Path("/interventions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InterventionResource {

    @Inject
    InterventionRepository interventionRepository;

    @Inject
    InterventionTaskRepository taskRepository;

    @Inject
    PatientRepository patientRepository;

    // ==================== Interventions ====================

    /**
     * POST /patients/{patientId}/interventions
     * Erstellt eine neue Intervention für einen Patienten.
     *
     * Request Body:
     * {
     *   "source": "ADL",
     *   "title": "Mobilität fördern",
     *   "description": "Tägliche Bewegungsübungen"
     * }
     *
     * Source-Werte: ANAMNESIS, ADL, SKILL, RESOURCE, CARE_NEED, MANUAL
     *
     * Basierend auf FSD Abschnitt 7 (Interventionen basieren auf Anamnese, ADLs, Skills, etc.)
     *
     * @param patientId Patient-ID
     * @param source Herkunft der Intervention
     * @param title Titel
     * @param description Beschreibung
     * @return 201 Created mit neuer Intervention
     */
    @POST
    @Path("/patients/{patientId}")
    public Response createIntervention(
            @PathParam("patientId") Long patientId,
            @QueryParam("source") String source,
            @QueryParam("title") String title,
            @QueryParam("description") String description) {

        // Validiere Patient
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        // Validiere Source
        InterventionSource interventionSource;
        try {
            interventionSource = InterventionSource.valueOf(source);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ungültige Source: " + source)
                    .build();
        }

        // Validiere Title
        if (title == null || title.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Titel darf nicht leer sein")
                    .build();
        }

        // Erstelle neue Intervention
        Intervention intervention = new Intervention(patient, interventionSource, title);
        if (description != null && !description.isBlank()) {
            intervention.setDescription(description);
        }
        interventionRepository.persist(intervention);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapToDTO(intervention))
                .build();
    }

    /**
     * GET /patients/{patientId}/interventions
     * Gibt alle aktiven Interventionen eines Patienten zurück.
     *
     * Basierend auf FSD Abschnitt 7 (Interventionen sind zentrale Planungseinheit)
     *
     * @param patientId Patient-ID
     * @return Liste aktiver Interventionen
     */
    @GET
    @Path("/patients/{patientId}")
    public Response getInterventionsForPatient(@PathParam("patientId") Long patientId) {
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        List<InterventionDTO> interventions = interventionRepository
                .findActiveByPatient(patient)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return Response.ok(interventions).build();
    }

    /**
     * GET /interventions/{id}
     * Gibt eine einzelne Intervention mit ihren Tasks zurück.
     *
     * @param id Intervention-ID
     * @return Intervention-Details
     */
    @GET
    @Path("/{id}")
    public Response getIntervention(@PathParam("id") Long id) {
        Intervention intervention = interventionRepository.findById(id);

        if (intervention == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Intervention nicht gefunden")
                    .build();
        }

        return Response.ok(mapToDTO(intervention)).build();
    }

    /**
     * PUT /interventions/{id}
     * Aktualisiert eine Intervention.
     *
     * @param id Intervention-ID
     * @param title Neuer Titel
     * @param description Neue Beschreibung
     * @return Aktualisierte Intervention
     */
    @PUT
    @Path("/{id}")
    public Response updateIntervention(
            @PathParam("id") Long id,
            @QueryParam("title") String title,
            @QueryParam("description") String description) {

        Intervention intervention = interventionRepository.findById(id);
        if (intervention == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Intervention nicht gefunden")
                    .build();
        }

        if (title != null && !title.isBlank()) {
            intervention.setTitle(title);
        }
        if (description != null && !description.isBlank()) {
            intervention.setDescription(description);
        }
        interventionRepository.persist(intervention);

        return Response.ok(mapToDTO(intervention)).build();
    }

    /**
     * DELETE /interventions/{id}
     * Deaktiviert eine Intervention.
     *
     * @param id Intervention-ID
     * @return 204 No Content
     */
    @DELETE
    @Path("/{id}")
    public Response deactivateIntervention(@PathParam("id") Long id) {
        Intervention intervention = interventionRepository.findById(id);
        if (intervention == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Intervention nicht gefunden")
                    .build();
        }

        interventionRepository.deactivateIntervention(id);

        return Response.noContent().build();
    }

    // ==================== Intervention Tasks ====================

    /**
     * POST /interventions/{interventionId}/tasks
     * Fügt eine neue Task zu einer Intervention hinzu.
     *
     * Request Body:
     * {
     *   "description": "Übung durchführen"
     * }
     *
     * Basierend auf FSD Abschnitt 7.2 (Interventionen können Pflegehandlungen enthalten)
     *
     * @param interventionId Intervention-ID
     * @param description Task-Beschreibung
     * @return 201 Created mit neuer Task
     */
    @POST
    @Path("/{interventionId}/tasks")
    public Response addTaskToIntervention(
            @PathParam("interventionId") Long interventionId,
            @QueryParam("description") String description) {

        // Validiere Intervention
        Intervention intervention = interventionRepository.findById(interventionId);
        if (intervention == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Intervention nicht gefunden")
                    .build();
        }

        // Validiere Description
        if (description == null || description.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Beschreibung darf nicht leer sein")
                    .build();
        }

        // Erstelle neue Task
        InterventionTask task = new InterventionTask(intervention, description);
        taskRepository.persist(task);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapTaskToDTO(task))
                .build();
    }

    /**
     * GET /interventions/{interventionId}/tasks
     * Gibt alle Tasks einer Intervention zurück.
     *
     * @param interventionId Intervention-ID
     * @return Liste aller Tasks
     */
    @GET
    @Path("/{interventionId}/tasks")
    public Response getTasksForIntervention(@PathParam("interventionId") Long interventionId) {
        Intervention intervention = interventionRepository.findById(interventionId);
        if (intervention == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Intervention nicht gefunden")
                    .build();
        }

        List<InterventionTaskDTO> tasks = taskRepository
                .findByIntervention(intervention)
                .stream()
                .map(this::mapTaskToDTO)
                .toList();

        return Response.ok(tasks).build();
    }

    /**
     * PUT /intervention-tasks/{taskId}/complete
     * Markiert eine Task als erledigt.
     *
     * Basierend auf FSD Abschnitt 7.2 (Können als durchgeführt markiert werden)
     *
     * @param taskId Task-ID
     * @return Aktualisierte Task
     */
    @PUT
    @Path("/tasks/{taskId}/complete")
    public Response completeTask(@PathParam("taskId") Long taskId) {
        InterventionTask task = taskRepository.findById(taskId);

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Task nicht gefunden")
                    .build();
        }

        task.markCompleted();
        taskRepository.persist(task);

        return Response.ok(mapTaskToDTO(task)).build();
    }

    // ==================== Mapping Methods ====================

    /**
     * Mappt Intervention Entity zu InterventionDTO
     */
    private InterventionDTO mapToDTO(Intervention intervention) {
        List<InterventionTaskDTO> tasks = intervention.getTasks()
                .stream()
                .map(this::mapTaskToDTO)
                .toList();

        return new InterventionDTO(
                intervention.getId(),
                intervention.getPatient().getId(),
                intervention.getSource().toString(),
                intervention.getTitle(),
                intervention.getDescription(),
                intervention.getActive(),
                intervention.getCreatedAt(),
                tasks
        );
    }

    /**
     * Mappt InterventionTask Entity zu InterventionTaskDTO
     */
    private InterventionTaskDTO mapTaskToDTO(InterventionTask task) {
        return new InterventionTaskDTO(
                task.getId(),
                task.getIntervention().getId(),
                task.getDescription(),
                task.getCompleted()
        );
    }
}





