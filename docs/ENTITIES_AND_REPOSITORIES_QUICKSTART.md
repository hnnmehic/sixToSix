# Entities & Repositories – Schnellstart

## Was wurde erstellt?

Basierend auf der technischen Spezifikation (TSD.adoc) und dem Datenmodell wurde eine vollständige Hibernate Panache Entity- und Repository-Schicht erstellt.

### Struktur

```
backend/src/main/java/at/htlleonding/sixtosix/
├── entity/     (21 Entities + 7 Enums)
└── repository/ (17 Repositories)
```

## Entities (Übersicht)

| Entity | Beschreibung | Key |
|--------|-------------|-----|
| UserAccount | Benutzerverwaltung (Keycloak) | id, keycloak_id |
| Patient | Zu betreuende Person | id |
| CareAssignment | Pfleger-Patient Zuordnung | id (unique: pfleger + patient) |
| Anamnesis | Globale Patientenanamnese | id (unique: patient) |
| AnamnesisVersion | Versionierte Anamnese-Einträge | id (version_number) |
| ADLDefinition | Vordefinierte ADLs | id, name |
| ADLAssessment | Bewertung von ADLs | id |
| SkillDefinition | Vordefinierte Skills | id, name |
| SkillAssessment | Bewertung von Skills | id |
| Resource | Soziale/persönliche Faktoren | id |
| Intervention | Pflegemaßnahmen | id |
| InterventionTask | Tasks einer Intervention | id |
| DailyPlan | Tagesstruktur | id (unique: patient + date) |
| DailyTask | Aktivität der Tagesstruktur | id |
| Medication | Medikamentenverwaltung | id |
| CareNeed | Ad-hoc Bedarf/Krisensituationen | id |
| AuditLog | Revisionssicherheit | id |

## Enums

| Enum | Werte |
|------|-------|
| UserRole | PFLEGER, PATIENT |
| AssessmentStatus | INTACT, RESTRICTED, NURSING_RELEVANT |
| InterventionSource | ANAMNESIS, ADL, SKILL, RESOURCE, CARE_NEED, MANUAL |
| ReminderLevel | NONE, ONCE, EVERY_15_MIN, EVERY_30_MIN |
| AuditAction | CREATE, UPDATE, DELETE, FINALIZE, APPROVE, CONFIRM, RESOLVE |

## Repositories (Auswahl)

Jedes Entity hat ein entsprechendes Repository mit:
- **CRUD-Operationen**: persist(), update(), delete(), findById()
- **Komplexe Queries**: find(), list(), count()
- **Custom Methods**: z.B. findByPatient(), findActive(), findLatestVersion()

Beispiele:
- `UserAccountRepository.findByKeycloakId()`
- `PatientRepository.findAllActive()`
- `CareAssignmentRepository.findActiveByPatient()`
- `AnamnesisVersionRepository.getNextVersionNumber()`
- `ResourceRepository.findPendingByPatient()`
- `InterventionRepository.findActiveByPatient()`
- `DailyPlanRepository.findByPatientAndDateRange()`

## Key Features

### ✅ Validierungen
Alle Entities verwenden Jakarta Validation:
```java
@NotNull, @NotBlank, @Past, @Positive
```

### ✅ Relationen
- One-to-Many / Many-to-One
- One-to-One
- Lazy Loading (FetchType.LAZY)
- Cascade Policies (CascadeType.ALL, CascadeType.REFRESH)
- orphanRemoval = true für Zugehörige

### ✅ Soft Deletes
Patient-Entities nutzen `deleted` Flag statt echter Löschung:
```java
public void softDelete(Long patientId) {
    Patient patient = findById(patientId);
    patient.setDeleted(true);
    persistAndFlush(patient);
}
```

### ✅ Versionierung
AnamnesisVersion mit Version Tracking:
```java
public Long getNextVersionNumber(Anamnesis anamnesis) {
    return findLatestVersion(anamnesis)
        .map(v -> v.getVersionNumber() + 1)
        .orElse(1L);
}
```

### ✅ Audit Trail
AuditLog für Revisionssicherheit:
- Wer hat was wann geändert?
- Immutable Records (performedAt nicht änderbar)
- Entity-Typ + ID + Action

### ✅ Business Methods
Entities enthalten Domain-Logik:
```java
// AnamnesisVersion
public void finalize() { this.finalized = true; }
public boolean canEdit() { return !finalized; }

// DailyTask
public void confirm() { 
    this.completed = true;
    this.confirmedAt = LocalDateTime.now();
}

// CareNeed
public void resolve() {
    this.active = false;
    this.resolvedAt = LocalDateTime.now();
}
```

## Nächste Schritte

1. **Flyway Migrationen**
   - Erstelle `src/main/resources/db/migration/V1__init_schema.sql`
   - Automatisches Schema-Setup beim Start

2. **REST Resources** (Controller)
   - POST /patients
   - GET /patients/{id}
   - POST /patients/{id}/anamnesis
   - etc.

3. **Service Layer** (optional)
   - Transaktionale Business-Logik
   - Autoriserung / Rights Checking

4. **Tests**
   - Unit Tests für Repositories
   - Integration Tests für REST APIs
   - Testcontainers für PostgreSQL

## MVC-Pattern

```
Request
   │
   ▼
REST Resource (Controller)
   │ ├─ @GET /patients
   │ ├─ @POST /patients
   │ └─ Validierung, Autorisierung
   │
   ▼
Repository (Datenzugriff)
   │ ├─ find(), list(), persist()
   │ ├─ Komplexe Queries
   │ └─ Lazy Loading
   │
   ▼
Entity (Datenmodell)
   │ ├─ Validierungen (@NotNull, etc.)
   │ ├─ Business Methods
   │ └─ Relationen (FK, Constraints)
   │
   ▼
PostgreSQL Database
```

## Dateien

- `docs/ENTITIES_AND_REPOSITORIES.md` – Vollständige Dokumentation
- `backend/src/main/java/at/htlleonding/sixtosix/entity/` – 21 Entities
- `backend/src/main/java/at/htlleonding/sixtosix/repository/` – 17 Repositories

## Fehler-Status

✅ **Keine Fehler** in allen Entities und Repositories.

IDE-Syntax Check: Alle Dateien validiert.

---

**Bereit für nächste Phase**: REST API (Resource Layer) kann jetzt erstellt werden.

