package at.htlleonding.sixtosix.resource;

import at.htlleonding.sixtosix.dto.AnamnesisDTO;
import at.htlleonding.sixtosix.dto.AnamnesisVersionDTO;
import at.htlleonding.sixtosix.entity.*;
import at.htlleonding.sixtosix.repository.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST Resource für Anamnesis-Operationen
 * Verwaltung der Patientenanamnese mit Versionierung
 *
 * Basierend auf FSD Abschnitt 4 (Anamnese)
 * und TSD Abschnitt 5.3 (anamnesis und anamnesis_version Tabellen)
 *
 * Endpunkte:
 * - POST   /patients/{patientId}/anamnesis           - Anamnese für Patient erstellen
 * - GET    /patients/{patientId}/anamnesis           - Anamnese lesen
 * - POST   /anamnesis/{anamnesisId}/versions         - Neue Version hinzufügen
 * - GET    /anamnesis/{anamnesisId}/versions         - Alle Versionen
 * - GET    /anamnesis/{anamnesisId}/versions/{vNum}  - Spezifische Version
 * - PUT    /anamnesis/versions/{versionId}/finalize  - Version finalisieren
 */
@Path("/anamnesis")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnamnesisResource {

    @Inject
    AnamnesisRepository anamnesisRepository;

    @Inject
    AnamnesisVersionRepository versionRepository;

    @Inject
    PatientRepository patientRepository;

    @Inject
    UserAccountRepository userAccountRepository;

    /**
     * POST /patients/{patientId}/anamnesis
     * Erstellt eine neue Anamnese für einen Patienten.
     *
     * Basierend auf FSD Abschnitt 4.1 (Pro Patient existiert eine globale Anamnese)
     *
     * @param patientId Patient-ID
     * @return 201 Created mit neuer Anamnese
     */
    @POST
    @Path("/patients/{patientId}")
    public Response createAnamnesis(@PathParam("patientId") Long patientId) {
        // Validiere Patient
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        // Prüfe ob Anamnese bereits existiert
        if (anamnesisRepository.existsByPatient(patient)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Anamnese existiert bereits für diesen Patienten")
                    .build();
        }

        // Erstelle neue Anamnese
        Anamnesis anamnesis = new Anamnesis(patient);
        anamnesisRepository.persist(anamnesis);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapToDTO(anamnesis))
                .build();
    }

    /**
     * GET /patients/{patientId}/anamnesis
     * Gibt die Anamnese eines Patienten zurück.
     *
     * Basierend auf FSD Abschnitt 4 (Dokumentation)
     *
     * @param patientId Patient-ID
     * @return Anamnese mit Versionsverlauf
     */
    @GET
    @Path("/patients/{patientId}")
    public Response getAnamnesisForPatient(@PathParam("patientId") Long patientId) {
        // Validiere Patient
        Patient patient = patientRepository.findById(patientId);
        if (patient == null || patient.getDeleted()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient nicht gefunden")
                    .build();
        }

        var anamnesis = anamnesisRepository.findByPatient(patient);
        if (anamnesis.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Anamnese nicht gefunden")
                    .build();
        }

        return Response.ok(mapToDTO(anamnesis.get())).build();
    }

    /**
     * POST /anamnesis/{anamnesisId}/versions
     * Fügt eine neue Version zu einer Anamnese hinzu.
     *
     * Request Body:
     * {
     *   "content": "Patient hat Bluthochdruck...",
     *   "createdById": 1
     * }
     *
     * Basierend auf FSD Abschnitt 4.2 (Versionierung)
     *
     * @param anamnesisId Anamnesis-ID
     * @param versionDTO Neue Version-Daten
     * @return 201 Created mit neuer Version
     */
    @POST
    @Path("/{anamnesisId}/versions")
    public Response addVersion(@PathParam("anamnesisId") Long anamnesisId, @Valid AnamnesisVersionDTO versionDTO) {
        // Validiere Anamnesis
        Anamnesis anamnesis = anamnesisRepository.findById(anamnesisId);
        if (anamnesis == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Anamnesis nicht gefunden")
                    .build();
        }

        // Validiere Creator
        UserAccount creator = userAccountRepository.findById(versionDTO.createdById());
        if (creator == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ersteller nicht gefunden")
                    .build();
        }

        // Ermittle nächste Versionsnummer
        Long nextVersionNumber = versionRepository.getNextVersionNumber(anamnesis);

        // Erstelle neue Version
        AnamnesisVersion newVersion = new AnamnesisVersion(
                anamnesis,
                nextVersionNumber,
                versionDTO.content(),
                creator
        );
        versionRepository.persist(newVersion);

        return Response
                .status(Response.Status.CREATED)
                .entity(mapVersionToDTO(newVersion))
                .build();
    }

    /**
     * GET /anamnesis/{anamnesisId}/versions
     * Gibt alle Versionen einer Anamnese zurück.
     *
     * Basierend auf FSD Abschnitt 4.2 (Alte Versionen bleiben nachvollziehbar)
     *
     * @param anamnesisId Anamnesis-ID
     * @return Liste aller Versionen
     */
    @GET
    @Path("/{anamnesisId}/versions")
    public Response getVersions(@PathParam("anamnesisId") Long anamnesisId) {
        Anamnesis anamnesis = anamnesisRepository.findById(anamnesisId);
        if (anamnesis == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Anamnesis nicht gefunden")
                    .build();
        }

        List<AnamnesisVersionDTO> versions = versionRepository
                .findVersionsByAnamnesis(anamnesis)
                .stream()
                .map(this::mapVersionToDTO)
                .toList();

        return Response.ok(versions).build();
    }

    /**
     * GET /anamnesis/{anamnesisId}/versions/{vNum}
     * Gibt eine spezifische Version einer Anamnese zurück.
     *
     * @param anamnesisId Anamnesis-ID
     * @param vNum Versionsnummer
     * @return Version-Details
     */
    @GET
    @Path("/{anamnesisId}/versions/{vNum}")
    public Response getVersion(@PathParam("anamnesisId") Long anamnesisId, @PathParam("vNum") Long vNum) {
        Anamnesis anamnesis = anamnesisRepository.findById(anamnesisId);
        if (anamnesis == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Anamnesis nicht gefunden")
                    .build();
        }

        var version = versionRepository.findByAnamnesisAndVersion(anamnesis, vNum);
        if (version.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Version nicht gefunden")
                    .build();
        }

        return Response.ok(mapVersionToDTO(version.get())).build();
    }

    /**
     * PUT /anamnesis/versions/{versionId}/finalize
     * Finalisiert eine Version (macht sie unveränderbar).
     *
     * Basierend auf FSD Abschnitt 4.3 (Nach Finalisierung ist der Eintrag nicht mehr änderbar)
     *
     * @param versionId AnamnesisVersion-ID
     * @return Finalisierte Version
     */
    @PUT
    @Path("/versions/{versionId}/finalize")
    public Response finalizeVersion(@PathParam("versionId") Long versionId) {
        AnamnesisVersion version = versionRepository.findById(versionId);

        if (version == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Version nicht gefunden")
                    .build();
        }

        if (version.getFinalized()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Version ist bereits finalisiert")
                    .build();
        }

        version.finalize();
        versionRepository.persist(version);

        return Response.ok(mapVersionToDTO(version)).build();
    }

    // ==================== Mapping Methods ====================

    /**
     * Mappt Anamnesis Entity zu AnamnesisDTO
     */
    private AnamnesisDTO mapToDTO(Anamnesis anamnesis) {
        List<AnamnesisVersionDTO> versions = anamnesis.getVersions()
                .stream()
                .map(this::mapVersionToDTO)
                .toList();

        return new AnamnesisDTO(
                anamnesis.getId(),
                anamnesis.getPatient().getId(),
                anamnesis.getCreatedAt(),
                versions
        );
    }

    /**
     * Mappt AnamnesisVersion Entity zu AnamnesisVersionDTO
     */
    private AnamnesisVersionDTO mapVersionToDTO(AnamnesisVersion version) {
        return new AnamnesisVersionDTO(
                version.getId(),
                version.getAnamnesis().getId(),
                version.getVersionNumber(),
                version.getContent(),
                version.getCreatedBy().getId(),
                version.getCreatedBy().getKeycloakId(),
                version.getCreatedAt(),
                version.getFinalized()
        );
    }
}




