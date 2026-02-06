package at.htlleonding.sixtosix.resource;

import at.htlleonding.sixtosix.dto.PatientCreateUpdateDTO;
import at.htlleonding.sixtosix.dto.PatientResponseDTO;
import at.htlleonding.sixtosix.dto.PatientDetailDTO;
import at.htlleonding.sixtosix.entity.Patient;
import at.htlleonding.sixtosix.repository.PatientRepository;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST Resource für Patient-Operationen
 * Basierend auf TSD Abschnitt 7 (REST API)
 *
 * Endpunkte:
 * - GET    /patients              - Alle aktiven Patienten
 * - POST   /patients              - Neuer Patient
 * - GET    /patients/{id}         - Patient (einfach)
 * - GET    /patients/{id}/details - Patient mit Relationen
 * - PUT    /patients/{id}         - Patient aktualisieren
 * - DELETE /patients/{id}         - Patient löschen (Soft Delete)
 */
@Path("/patients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientResource {

    @Inject
    PatientRepository patientRepository;

    /**
     * GET /patients
     * Gibt alle aktiven (nicht gelöschten) Patienten zurück.
     *
     * @return Liste aller Patienten (einfache View)
     */
    @GET
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAllActive()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * POST /patients
     * Erstellt einen neuen Patienten.
     *
     * @param createDTO Input DTO mit Validierung
     * @return 201 Created mit neuer Patient-Ressource
     */
    @POST
    public Response createPatient(@Valid PatientCreateUpdateDTO createDTO) {
        Patient patient = new Patient(
                createDTO.firstname(),
                createDTO.lastname(),
                createDTO.birthdate()
        );
        patientRepository.persist(patient);

        PatientResponseDTO responseDTO = mapToResponseDTO(patient);

        return Response
                .status(Response.Status.CREATED)
                .entity(responseDTO)
                .build();
    }

    /**
     * GET /patients/{id}
     * Gibt einen einzelnen Patienten zurück (einfache View ohne Relationen).
     *
     * @param id Patient-ID
     * @return Patient-Daten
     */
    @GET
    @Path("/{id}")
    public Response getPatient(@PathParam("id") Long id) {
        Patient patient = patientRepository.findById(id);

        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        return Response.ok(mapToResponseDTO(patient)).build();
    }

    /**
     * GET /patients/{id}/details
     * Gibt einen Patienten mit allen Relationen zurück (Detail-View).
     *
     * Basierend auf FSD Abschnitt 4 (Dokumentation & Nachvollziehbarkeit)
     *
     * @param id Patient-ID
     * @return Patient mit Anamnesis, ADLs, Skills, Resources, Interventions, etc.
     */
    @GET
    @Path("/{id}/details")
    public Response getPatientDetails(@PathParam("id") Long id) {
        Patient patient = patientRepository.findById(id);

        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        return Response.ok(mapToDetailDTO(patient)).build();
    }

    /**
     * PUT /patients/{id}
     * Aktualisiert einen Patienten.
     *
     * @param id Patient-ID
     * @param updateDTO Update-Daten mit Validierung
     * @return Aktualisierte Patient-Daten
     */
    @PUT
    @Path("/{id}")
    public Response updatePatient(@PathParam("id") Long id, @Valid PatientCreateUpdateDTO updateDTO) {
        Patient patient = patientRepository.findById(id);

        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        patient.setFirstname(updateDTO.firstname());
        patient.setLastname(updateDTO.lastname());
        patient.setBirthdate(updateDTO.birthdate());
        patientRepository.persist(patient);

        return Response.ok(mapToResponseDTO(patient)).build();
    }

    /**
     * DELETE /patients/{id}
     * Löscht einen Patienten (Soft Delete).
     *
     * Basierend auf TSD Abschnitt 5.1 (Soft Deletes)
     *
     * @param id Patient-ID
     * @return 204 No Content
     */
    @DELETE
    @Path("/{id}")
    public Response deletePatient(@PathParam("id") Long id) {
        Patient patient = patientRepository.findById(id);

        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        patientRepository.softDelete(id);

        return Response.noContent().build();
    }

    // ==================== Mapping Methods ====================

    /**
     * Mappt Patient Entity zu PatientResponseDTO (einfache View)
     */
    private PatientResponseDTO mapToResponseDTO(Patient patient) {
        return new PatientResponseDTO(
                patient.getId(),
                patient.getFirstname(),
                patient.getLastname(),
                patient.getBirthdate(),
                patient.getDeleted()
        );
    }

    /**
     * Mappt Patient Entity zu PatientDetailDTO (mit Relationen)
     */
    private PatientDetailDTO mapToDetailDTO(Patient patient) {
        return new PatientDetailDTO(
                patient.getId(),
                patient.getFirstname(),
                patient.getLastname(),
                patient.getBirthdate(),
                patient.getDeleted(),
                null, // careAssignments - später implementieren
                null, // anamnesis - später implementieren
                null, // adlAssessments - später implementieren
                null, // resources - später implementieren
                null, // interventions - später implementieren
                null  // medications - später implementieren
        );
    }
}



