package at.htlleonding.sixtosix.resource;

import at.htlleonding.sixtosix.dto.DailyPlanDTO;
import at.htlleonding.sixtosix.dto.DailyTaskDTO;
import at.htlleonding.sixtosix.entity.*;
import at.htlleonding.sixtosix.repository.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Resource für DailyPlan-Operationen
 * Verwaltung der Tagesstruktur und deren Aufgaben
 *
 * Basierend auf FSD Abschnitt 8 (Tagesstruktur und Erinnerungen)
 * und TSD Abschnitt 5.8 (daily_plan und daily_task Tabellen)
 *
 * Endpunkte:
 * - POST   /patients/{patientId}/daily-plans              - Neuer Tagesplan
 * - GET    /patients/{patientId}/daily-plans              - Alle Tagespläne eines Patienten
 * - GET    /patients/{patientId}/daily-plans/{date}       - Tagesplan für Datum
 * - GET    /patients/{patientId}/daily-plans/today        - Heutiger Plan
 * - POST   /daily-plans/{planId}/tasks                   - Task zum Plan hinzufügen
 * - PUT    /daily-tasks/{taskId}/confirm                 - Task bestätigen
 */
@Path("/daily-plans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DailyPlanResource {

    @Inject
    DailyPlanRepository dailyPlanRepository;

    @Inject
    DailyTaskRepository taskRepository;

    @Inject
    PatientRepository patientRepository;

    // ==================== Daily Plans ====================

    /**
     * POST /patients/{patientId}/daily-plans
     * Erstellt einen neuen Tagesplan für einen Patienten.
     *
     * Query Parameter:
     * - date: Plannungsdatum (Format: YYYY-MM-DD)
     *
     * Basierend auf FSD Abschnitt 8.1 (Tagesstruktur wird gemeinsam erstellt)
     *
     * @param patientId Patient-ID
     * @param date Plannungsdatum
     * @return 201 Created mit neuem Tagesplan
     */
    @POST
    @Path("/patients/{patientId}")
    public Response createDailyPlan(
            @PathParam("patientId") Long patientId,
            @QueryParam("date") String date) {

        // Validiere Patient
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        // Parse Datum
        LocalDate planDate;
        try {
            planDate = LocalDate.parse(date);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ungültiges Datum-Format (erwartet: YYYY-MM-DD)")
                    .build();
        }

        // Prüfe ob Plan für diesen Tag bereits existiert
        var existingPlan = dailyPlanRepository.findByPatientAndDate(patient, planDate);
        if (existingPlan.isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Tagesplan für dieses Datum existiert bereits")
                    .build();
        }

        // Erstelle neuen Tagesplan
        DailyPlan dailyPlan = new DailyPlan(patient, planDate);
        dailyPlanRepository.persist(dailyPlan);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapToDTO(dailyPlan))
                .build();
    }

    /**
     * GET /patients/{patientId}/daily-plans
     * Gibt alle Tagespläne eines Patienten zurück.
     *
     * @param patientId Patient-ID
     * @return Liste aller Tagespläne
     */
    @GET
    @Path("/patients/{patientId}")
    public Response getDailyPlansForPatient(@PathParam("patientId") Long patientId) {
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        List<DailyPlanDTO> plans = dailyPlanRepository
                .findByPatient(patient)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return Response.ok(plans).build();
    }

    /**
     * GET /patients/{patientId}/daily-plans/{date}
     * Gibt den Tagesplan für ein spezifisches Datum zurück.
     *
     * Query Parameter:
     * - date: Plannungsdatum (Format: YYYY-MM-DD)
     *
     * @param patientId Patient-ID
     * @param date Plannungsdatum
     * @return Tagesplan für Datum oder 404
     */
    @GET
    @Path("/patients/{patientId}/by-date")
    public Response getDailyPlanForDate(
            @PathParam("patientId") Long patientId,
            @QueryParam("date") String date) {

        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        // Parse Datum
        LocalDate planDate;
        try {
            planDate = LocalDate.parse(date);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ungültiges Datum-Format (erwartet: YYYY-MM-DD)")
                    .build();
        }

        var plan = dailyPlanRepository.findByPatientAndDate(patient, planDate);
        if (plan.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Tagesplan für dieses Datum nicht gefunden")
                    .build();
        }

        return Response.ok(mapToDTO(plan.get())).build();
    }

    /**
     * GET /patients/{patientId}/daily-plans/today
     * Gibt den Tagesplan für heute zurück.
     *
     * Basierend auf FSD Abschnitt 8 (Tagesstruktur unterstützt Patient im Tagesablauf)
     *
     * @param patientId Patient-ID
     * @return Heutiger Tagesplan oder 404
     */
    @GET
    @Path("/patients/{patientId}/today")
    public Response getTodaysDailyPlan(@PathParam("patientId") Long patientId) {
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        var plan = dailyPlanRepository.findTodaysPlan(patient);
        if (plan.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Kein Tagesplan für heute")
                    .build();
        }

        return Response.ok(mapToDTO(plan.get())).build();
    }

    // ==================== Daily Tasks ====================

    /**
     * POST /daily-plans/{planId}/tasks
     * Fügt eine neue Task zum Tagesplan hinzu.
     *
     * Query Parameter:
     * - title: Titel der Aufgabe
     * - reminderLevel: Erinnerungsstufe (NONE, ONCE, EVERY_15_MIN, EVERY_30_MIN)
     *
     * Basierend auf FSD Abschnitt 8 (Einzelne Aktivitäten können eingeplant werden)
     * und FSD Abschnitt 8.3 (Erinnerungen sind nur für alltägliche Tätigkeiten erlaubt)
     *
     * @param planId Daily Plan-ID
     * @param title Task-Titel
     * @param reminderLevel Erinnerungsstufe
     * @return 201 Created mit neuer Task
     */
    @POST
    @Path("/{planId}/tasks")
    public Response addTaskToDailyPlan(
            @PathParam("planId") Long planId,
            @QueryParam("title") String title,
            @QueryParam("reminderLevel") String reminderLevel) {

        // Validiere Plan
        DailyPlan dailyPlan = dailyPlanRepository.findById(planId);
        if (dailyPlan == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Tagesplan nicht gefunden")
                    .build();
        }

        // Validiere Title
        if (title == null || title.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Titel darf nicht leer sein")
                    .build();
        }

        // Validiere ReminderLevel
        ReminderLevel reminderLvl;
        try {
            reminderLvl = ReminderLevel.valueOf(reminderLevel != null ? reminderLevel : "NONE");
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ungültige Erinnerungsstufe: " + reminderLevel)
                    .build();
        }

        // Erstelle neue Task
        DailyTask task = new DailyTask(dailyPlan, title, reminderLvl);
        taskRepository.persist(task);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapTaskToDTO(task))
                .build();
    }

    /**
     * GET /daily-plans/{planId}/tasks
     * Gibt alle Tasks eines Tagesplans zurück.
     *
     * @param planId Daily Plan-ID
     * @return Liste aller Tasks
     */
    @GET
    @Path("/{planId}/tasks")
    public Response getTasksForDailyPlan(@PathParam("planId") Long planId) {
        DailyPlan dailyPlan = dailyPlanRepository.findById(planId);
        if (dailyPlan == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Tagesplan nicht gefunden")
                    .build();
        }

        List<DailyTaskDTO> tasks = taskRepository
                .findByDailyPlan(dailyPlan)
                .stream()
                .map(this::mapTaskToDTO)
                .toList();

        return Response.ok(tasks).build();
    }

    /**
     * PUT /daily-tasks/{taskId}/confirm
     * Bestätigt eine Daily Task als durchgeführt.
     *
     * Basierend auf FSD Abschnitt 8.2 (Tätigkeiten können manuell bestätigt werden)
     *
     * @param taskId Daily Task-ID
     * @return Bestätigte Task
     */
    @PUT
    @Path("/tasks/{taskId}/confirm")
    public Response confirmTask(@PathParam("taskId") Long taskId) {
        DailyTask task = taskRepository.findById(taskId);

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Task nicht gefunden")
                    .build();
        }

        if (task.getCompleted()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Task ist bereits bestätigt")
                    .build();
        }

        task.confirm();
        taskRepository.persist(task);

        return Response.ok(mapTaskToDTO(task)).build();
    }

    // ==================== Mapping Methods ====================

    /**
     * Mappt DailyPlan Entity zu DailyPlanDTO
     */
    private DailyPlanDTO mapToDTO(DailyPlan dailyPlan) {
        List<DailyTaskDTO> tasks = dailyPlan.getTasks()
                .stream()
                .map(this::mapTaskToDTO)
                .toList();

        return new DailyPlanDTO(
                dailyPlan.getId(),
                dailyPlan.getPatient().getId(),
                dailyPlan.getPlanDate(),
                tasks
        );
    }

    /**
     * Mappt DailyTask Entity zu DailyTaskDTO
     */
    private DailyTaskDTO mapTaskToDTO(DailyTask task) {
        return new DailyTaskDTO(
                task.getId(),
                task.getDailyPlan().getId(),
                task.getTitle(),
                task.getReminderLevel().toString(),
                task.getCompleted(),
                task.getConfirmedAt()
        );
    }
}




