# DTOs – Dokumentation

DTOs (Data Transfer Objects) dienen der Entkopplung zwischen Entities und REST API.

## Struktur

```
backend/src/main/java/at/htlleonding/sixtosix/dto/
├── PatientCreateUpdateDTO       (Record - Input für POST/PUT)
├── PatientResponseDTO           (Record - Output für GET)
├── PatientDetailDTO             (Record - Output mit Relationen)
├── CareAssignmentDTO            (Record - Pfleger-Patient Zuordnung)
├── AnamnesisDTO                 (Record - Patientenanamnese)
├── AnamnesisVersionDTO          (Record - Versionierte Anamnese)
├── ADLDefinitionDTO             (Record - Vordefinierte ADLs)
├── ADLAssessmentDTO             (Record - ADL Bewertung)
├── SkillDefinitionDTO           (Record - Vordefinierte Skills)
├── SkillAssessmentDTO           (Record - Skill Bewertung)
├── ResourceDTO                  (Record - Ressourcen)
├── InterventionDTO              (Record - Interventionen)
├── InterventionTaskDTO          (Record - Intervention Tasks)
├── DailyPlanDTO                 (Record - Tagesstruktur)
├── DailyTaskDTO                 (Record - Tagesaufgaben)
├── MedicationDTO                (Record - Medikation)
├── CareNeedDTO                  (Record - Bedarf/Krise)
└── UserAccountDTO               (Record - Benutzer)
```

## DTOs als Records

Alle DTOs sind als **Java Records** implementiert (seit Java 16):
- ✅ Immutable (unveränderlich)
- ✅ Kompakt (minimal boilerplate code)
- ✅ Automatische equals, hashCode, toString
- ✅ Automatische Komponenten-Accessoren (firstname(), lastname(), etc.)
- ✅ Validierungen via Jakarta Annotations

## Beispiel: PatientCreateUpdateDTO

```java
public record PatientCreateUpdateDTO(
    @NotBlank(message = "Vorname darf nicht leer sein")
    String firstname,
    
    @NotBlank(message = "Nachname darf nicht leer sein")
    String lastname,
    
    @NotNull(message = "Geburtsdatum darf nicht null sein")
    @Past(message = "Geburtsdatum muss in der Vergangenheit liegen")
    LocalDate birthdate
) {
}
```

Verwendung:
```json
{
  "firstname": "Max",
  "lastname": "Mustermann",
  "birthdate": "1990-01-01"
}
```

## Validierungen

Alle Input-DTOs verwenden Jakarta Validation:
- `@NotNull` - Feld darf nicht null sein
- `@NotBlank` - String darf nicht leer sein
- `@Positive` - Zahl muss positiv sein
- `@Past` - Datum muss in der Vergangenheit liegen
- Deutsche Meldungen in `message` Attribut

## DTO-Kategorien

### Input DTOs (für POST/PUT)
- PatientCreateUpdateDTO
- AnamnesisVersionDTO
- ADLAssessmentDTO
- etc.

### Output DTOs (für GET)
- PatientResponseDTO (einfache Antwort)
- PatientDetailDTO (mit Relationen)
- CareAssignmentDTO
- AnamnesisDTO
- etc.

## Mapping: Entity → DTO

Beispiel für ein einfaches Mapping:

```java
// Entity zu DTO
Patient entity = new Patient(...);
PatientResponseDTO dto = new PatientResponseDTO(
    entity.getId(),
    entity.getFirstname(),
    entity.getLastname(),
    entity.getBirthdate(),
    entity.getDeleted()
);

// DTO zu Entity (für Erstellung)
PatientCreateUpdateDTO dto = new PatientCreateUpdateDTO(...);
Patient entity = new Patient(
    dto.firstname(),
    dto.lastname(),
    dto.birthdate()
);
```

Später: MapStruct oder ModelMapper für automatisches Mapping.

## Records vs. Klassen

| Aspekt | Records | Klassen |
|--------|---------|---------|
| Boilerplate | ❌ Minimal | ✅ Viel |
| Immutability | ✅ Immer | ❌ Optional |
| equals/hashCode | ✅ Auto | ❌ Manuell |
| toString | ✅ Auto | ❌ Manuell |
| Getter | ✅ Auto | ❌ Manuell |
| Serialisierung | ✅ Gut | ✅ Gut |
| JSON | ✅ Jackson kompatibel | ✅ Jackson kompatibel |

## Verwendung in REST Resources

```java
@Path("/patients")
public class PatientResource {
    
    @POST
    public PatientResponseDTO create(PatientCreateUpdateDTO createDTO) {
        // Validation erfolgt automatisch via @Valid
        Patient entity = mapToEntity(createDTO);
        patient patientSaved = repo.persist(entity);
        return mapToResponseDTO(patientSaved);
    }
    
    @GET @Path("/{id}")
    public PatientResponseDTO getById(@PathParam("id") Long id) {
        Patient entity = repo.findById(id);
        return mapToResponseDTO(entity);
    }
    
    @GET @Path("/{id}/details")
    public PatientDetailDTO getDetails(@PathParam("id") Long id) {
        Patient entity = repo.findById(id);
        return mapToDetailDTO(entity);
    }
}
```

## Validierung

Quarkus validiert automatisch bei:
```java
@POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response create(@Valid PatientCreateUpdateDTO createDTO) {
    // createDTO ist bereits validiert
    // Bei Validierungsfehlern → 400 Bad Request
}
```

## Best Practices

### ✅ Gut
- Ein DTO pro Endpoint-Verhalten
- Separate DTOs für Input und Output
- Validierungen in Input-DTOs
- Immutable Records
- Keine Geschäftslogik in DTOs

### ❌ Falsch
- Entity direkt als REST Response
- Zu viele Felder in einem DTO
- Mutable DTOs
- Bidirektionale Relationen in DTOs
- Geschäftslogik in DTOs

## Größere DTOs mit vielen Feldern

Beispiel PatientDetailDTO:
```java
public record PatientDetailDTO(
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
) {
}
```

Diese DTOs können für Detailansichten verwendet werden.

## Zukünftige Verbesserungen

1. **MapStruct Integration**
   ```java
   @Mapper(componentModel = "quarkus")
   public interface PatientMapper {
       PatientResponseDTO toDTO(Patient entity);
       Patient toEntity(PatientCreateUpdateDTO dto);
   }
   ```

2. **Paging DTOs**
   ```java
   public record PageDTO<T>(
       List<T> content,
       long totalElements,
       int totalPages
   ) {}
   ```

3. **Error DTOs**
   ```java
   public record ErrorDTO(
       String message,
       String code,
       LocalDateTime timestamp
   ) {}
   ```

## Zusammenfassung

- ✅ 18 DTOs als Java Records
- ✅ Immutable und validierbar
- ✅ Minimal boilerplate code
- ✅ Sichere Entkopplung (Entities ≠ API)
- ✅ Flexibles Datenmodell
- ✅ Zukunftssicher (MapStruct ready)

**Bereit für REST API Implementation!**

