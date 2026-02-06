# DTOs – Schnellreferenz

## Alle DTOs auf einen Blick

### Patient-bezogene DTOs

#### PatientCreateUpdateDTO (Input)
```java
record PatientCreateUpdateDTO(
    @NotBlank String firstname,
    @NotBlank String lastname,
    @Past LocalDate birthdate
)
```

#### PatientResponseDTO (Output - einfach)
```java
record PatientResponseDTO(
    Long id,
    String firstname,
    String lastname,
    LocalDate birthdate,
    Boolean deleted
)
```

#### PatientDetailDTO (Output - mit Relationen)
```java
record PatientDetailDTO(
    Long id,
    String firstname,
    String lastname,
    LocalDate birthdate,
    Boolean deleted,
    List<CareAssignmentDTO> careAssignments,
    AnamnesisDTO anamnesis,
    List<ADLAssessmentDTO> adlAssessments,
    List<ResourceDTO> resources,
    List<InterventionDTO> interventions,
    List<MedicationDTO> medications
)
```

### Benutzer & Zuordnungen

#### UserAccountDTO (Output)
```java
record UserAccountDTO(
    Long id,
    String keycloakId,
    String role
)
```

#### CareAssignmentDTO (Output)
```java
record CareAssignmentDTO(
    Long id,
    Long pflegerUserId,
    String pflegerName,
    Long patientId,
    String patientName,
    Boolean active,
    LocalDateTime createdAt
)
```

### Anamnesis

#### AnamnesisDTO (Output)
```java
record AnamnesisDTO(
    Long id,
    Long patientId,
    LocalDateTime createdAt,
    List<AnamnesisVersionDTO> versions
)
```

#### AnamnesisVersionDTO (Input/Output)
```java
record AnamnesisVersionDTO(
    Long id,
    Long anamnesisId,
    @Positive Long versionNumber,
    @NotBlank String content,
    Long createdById,
    String createdByName,
    LocalDateTime createdAt,
    Boolean finalized
)
```

### ADL

#### ADLDefinitionDTO (Output)
```java
record ADLDefinitionDTO(
    Long id,
    String name
)
```

#### ADLAssessmentDTO (Output)
```java
record ADLAssessmentDTO(
    Long id,
    Long adlDefinitionId,
    String adlName,
    Long patientId,
    String status,
    LocalDateTime assessedAt,
    Long assessedById,
    String assessedByName
)
```

### Skills

#### SkillDefinitionDTO (Output)
```java
record SkillDefinitionDTO(
    Long id,
    String name
)
```

#### SkillAssessmentDTO (Output)
```java
record SkillAssessmentDTO(
    Long id,
    Long skillDefinitionId,
    String skillName,
    Long patientId,
    String status,
    String comment,
    LocalDateTime assessedAt
)
```

### Resources

#### ResourceDTO (Output)
```java
record ResourceDTO(
    Long id,
    Long patientId,
    String type,
    String description,
    Long proposedById,
    String proposedByName,
    Boolean approved,
    LocalDateTime createdAt
)
```

### Interventionen

#### InterventionDTO (Output)
```java
record InterventionDTO(
    Long id,
    Long patientId,
    String source,
    String title,
    String description,
    Boolean active,
    LocalDateTime createdAt,
    List<InterventionTaskDTO> tasks
)
```

#### InterventionTaskDTO (Output)
```java
record InterventionTaskDTO(
    Long id,
    Long interventionId,
    String description,
    Boolean completed
)
```

### Tagesstruktur

#### DailyPlanDTO (Output)
```java
record DailyPlanDTO(
    Long id,
    Long patientId,
    LocalDate planDate,
    List<DailyTaskDTO> tasks
)
```

#### DailyTaskDTO (Output)
```java
record DailyTaskDTO(
    Long id,
    Long dailyPlanId,
    String title,
    String reminderLevel,
    Boolean completed,
    LocalDateTime confirmedAt
)
```

### Weitere

#### MedicationDTO (Output)
```java
record MedicationDTO(
    Long id,
    Long patientId,
    String name,
    String dosage,
    String intakeTime,
    Boolean confirmable,
    LocalDateTime createdAt
)
```

#### CareNeedDTO (Output)
```java
record CareNeedDTO(
    Long id,
    Long patientId,
    String description,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime resolvedAt
)
```

## Verwendungsbeispiel in REST Resource

```java
@Path("/patients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientResource {
    
    @Inject PatientRepository patientRepo;
    
    // POST - Neuer Patient
    @POST
    public Response createPatient(@Valid PatientCreateUpdateDTO createDTO) {
        Patient entity = new Patient(
            createDTO.firstname(),
            createDTO.lastname(),
            createDTO.birthdate()
        );
        entity.persist();
        
        return Response
            .status(Response.Status.CREATED)
            .entity(mapToResponseDTO(entity))
            .build();
    }
    
    // GET - Patient Liste
    @GET
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepo.findAllActive()
            .stream()
            .map(this::mapToResponseDTO)
            .toList();
    }
    
    // GET - Einzelner Patient (einfach)
    @GET
    @Path("/{id}")
    public PatientResponseDTO getPatient(@PathParam("id") Long id) {
        Patient entity = patientRepo.findById(id);
        if (entity == null) {
            throw new NotFoundException("Patient nicht gefunden");
        }
        return mapToResponseDTO(entity);
    }
    
    // GET - Einzelner Patient (mit Details)
    @GET
    @Path("/{id}/details")
    public PatientDetailDTO getPatientDetails(@PathParam("id") Long id) {
        Patient entity = patientRepo.findById(id);
        if (entity == null) {
            throw new NotFoundException("Patient nicht gefunden");
        }
        return mapToDetailDTO(entity);
    }
    
    // PUT - Patient aktualisieren
    @PUT
    @Path("/{id}")
    public PatientResponseDTO updatePatient(
        @PathParam("id") Long id,
        @Valid PatientCreateUpdateDTO updateDTO
    ) {
        Patient entity = patientRepo.findById(id);
        if (entity == null) {
            throw new NotFoundException("Patient nicht gefunden");
        }
        
        entity.setFirstname(updateDTO.firstname());
        entity.setLastname(updateDTO.lastname());
        entity.setBirthdate(updateDTO.birthdate());
        entity.persist();
        
        return mapToResponseDTO(entity);
    }
    
    // Private Mapping-Methoden
    private PatientResponseDTO mapToResponseDTO(Patient entity) {
        return new PatientResponseDTO(
            entity.getId(),
            entity.getFirstname(),
            entity.getLastname(),
            entity.getBirthdate(),
            entity.getDeleted()
        );
    }
    
    private PatientDetailDTO mapToDetailDTO(Patient entity) {
        return new PatientDetailDTO(
            entity.getId(),
            entity.getFirstname(),
            entity.getLastname(),
            entity.getBirthdate(),
            entity.getDeleted(),
            mapCareAssignments(entity.getCareAssignments()),
            mapAnamnesis(entity.getAnamnesis()),
            mapADLAssessments(entity.getAdlAssessments()),
            mapResources(entity.getResources()),
            mapInterventions(entity.getInterventions()),
            mapMedications(entity.getMedications())
        );
    }
    
    // ... weitere Mapping-Methoden
}
```

## Best Practices

### ✅ Input DTO
- Verwendet `@Valid` in Resource
- Hat Validierungen (@NotNull, @NotBlank, etc.)
- Wird zu Entity gemappt

### ✅ Output DTO
- Enthält IDs von Relationen (keine ganzen Objekte)
- Unterschiedliche Ebenen (simple vs. detail)
- Keine sensitiven Daten

### ✅ Records
- Immutable (final)
- Automatische equals(), hashCode(), toString()
- Komponentenaccessoren (firstname() statt getFirstname())
- Validierungen in Parametern

## Dateienliste

```
backend/src/main/java/at/htlleonding/sixtosix/dto/
├── PatientCreateUpdateDTO.java
├── PatientResponseDTO.java
├── PatientDetailDTO.java
├── UserAccountDTO.java
├── CareAssignmentDTO.java
├── AnamnesisDTO.java
├── AnamnesisVersionDTO.java
├── ADLDefinitionDTO.java
├── ADLAssessmentDTO.java
├── SkillDefinitionDTO.java
├── SkillAssessmentDTO.java
├── ResourceDTO.java
├── InterventionDTO.java
├── InterventionTaskDTO.java
├── DailyPlanDTO.java
├── DailyTaskDTO.java
├── MedicationDTO.java
└── CareNeedDTO.java
```

**18 DTOs gesamt - Alle als Java Records implementiert**

