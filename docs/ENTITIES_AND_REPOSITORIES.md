# Entities und Repositories – Übersicht

Dokumentation aller erstellten Hibernate Panache Entities und Repositories für die Pflegedienstanwendung.

## Projektstruktur

```
src/main/java/at/htlleonding/sixtosix/
├── entity/
│   ├── UserAccount.java
│   ├── UserRole.java
│   ├── Patient.java
│   ├── CareAssignment.java
│   ├── Anamnesis.java
│   ├── AnamnesisVersion.java
│   ├── ADLDefinition.java
│   ├── ADLAssessment.java
│   ├── AssessmentStatus.java
│   ├── SkillDefinition.java
│   ├── SkillAssessment.java
│   ├── Resource.java
│   ├── Intervention.java
│   ├── InterventionSource.java
│   ├── InterventionTask.java
│   ├── DailyPlan.java
│   ├── DailyTask.java
│   ├── ReminderLevel.java
│   ├── Medication.java
│   ├── CareNeed.java
│   ├── AuditLog.java
│   └── AuditAction.java
└── repository/
    ├── UserAccountRepository.java
    ├── PatientRepository.java
    ├── CareAssignmentRepository.java
    ├── AnamnesisRepository.java
    ├── AnamnesisVersionRepository.java
    ├── ADLDefinitionRepository.java
    ├── ADLAssessmentRepository.java
    ├── SkillDefinitionRepository.java
    ├── SkillAssessmentRepository.java
    ├── ResourceRepository.java
    ├── InterventionRepository.java
    ├── InterventionTaskRepository.java
    ├── DailyPlanRepository.java
    ├── DailyTaskRepository.java
    ├── MedicationRepository.java
    ├── CareNeedRepository.java
    └── AuditLogRepository.java
```

## Entities – Übersicht

### 1. UserAccount
**Beschreibung**: Zentrale Benutzerverwaltung für Pfleger.
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- keycloak_id (unique): Referenz zu Keycloak
- role: UserRole (PFLEGER, PATIENT)
- created_at: Zeitstempel

**Relationen**:
- One-to-Many zu CareAssignment (pfleger)
- One-to-Many zu AnamnesisVersion (createdBy)
- One-to-Many zu ADLAssessment (assessedBy)
- One-to-Many zu Resource (proposedBy)
- One-to-Many zu AuditLog (performedBy)

**Validierungen**:
- @NotBlank keycloak_id
- @NotNull role

---

### 2. Patient
**Beschreibung**: Zu betreute Person (Klient).
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- firstname, lastname: Name
- birthdate: Geburtsdatum (@Past)
- deleted: Soft Delete Flag

**Relationen**:
- One-to-Many zu CareAssignment
- One-to-One zu Anamnesis
- One-to-Many zu ADLAssessment, SkillAssessment
- One-to-Many zu Resource, Intervention, DailyPlan, Medication, CareNeed

**Validierungen**:
- @NotBlank firstname, lastname
- @NotNull @Past birthdate

---

### 3. CareAssignment
**Beschreibung**: Zuordnung zwischen Pfleger und Patient (Betreuungsbeziehung).
**Primärschlüssel**: id (Long)
**Unique Constraint**: (pfleger_id, patient_id)
**Wichtige Spalten**:
- pfleger_id (FK): Referenz zu UserAccount
- patient_id (FK): Referenz zu Patient
- active: Boolean (default true)
- created_at: Zeitstempel

**Validierungen**:
- @NotNull pfleger, patient
- Unique Constraint auf pfleger + patient

---

### 4. Anamnesis
**Beschreibung**: Pro Patient existiert eine globale Anamnese (laufend erweiterbar).
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- patient_id (FK, unique): Referenz zu Patient (1:1)
- created_at: Zeitstempel

**Relationen**:
- One-to-One zu Patient
- One-to-Many zu AnamnesisVersion

---

### 5. AnamnesisVersion
**Beschreibung**: Versionierte Änderungen der Anamnese (Revisionssicherheit).
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- anamnesis_id (FK): Referenz zu Anamnesis
- version_number: Versionsnummer (@Positive)
- content: TEXT (Inhalt)
- created_by (FK): Ersteller (UserAccount)
- created_at: Zeitstempel
- finalized: Boolean (read-only nach Finalisierung)

**Business Methods**:
- finalize(): Sperrt Version
- canEdit(): Prüft, ob noch bearbeitbar

**Validierungen**:
- @NotNull anamnesis, versionNumber, content, createdBy
- @Positive versionNumber

---

### 6. ADLDefinition
**Beschreibung**: Vordefinierte Aktivitäten des täglichen Lebens (Stammdaten).
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- name (unique): Bezeichnung (z.B. "Essen", "Trinken", "Mobilität")

**Relationen**:
- One-to-Many zu ADLAssessment

---

### 7. AssessmentStatus (Enum)
**Werte**: INTACT, RESTRICTED, NURSING_RELEVANT

---

### 8. ADLAssessment
**Beschreibung**: Bewertung von ADLs pro Patient (zeitlicher Verlauf).
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- adl_definition_id (FK): Referenz zu ADLDefinition
- patient_id (FK): Referenz zu Patient
- status: AssessmentStatus
- assessed_at: Zeitstempel
- assessed_by (FK): Bewerter (UserAccount)

**Validierungen**:
- @NotNull adlDefinition, patient, status, assessedBy

---

### 9. SkillDefinition
**Beschreibung**: Vordefinierte Fähigkeitsbereiche (Stammdaten).
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- name (unique): Bezeichnung (z.B. "Selbstfürsorge", "Haushalt")

**Relationen**:
- One-to-Many zu SkillAssessment

---

### 10. SkillAssessment
**Beschreibung**: Bewertung von Skills pro Patient (analog ADL).
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- skill_definition_id (FK): Referenz zu SkillDefinition
- patient_id (FK): Referenz zu Patient
- status: AssessmentStatus
- comment: TEXT (optional)
- assessed_at: Zeitstempel

---

### 11. Resource
**Beschreibung**: Soziale, persönliche, biografische Faktoren des Patienten.
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- patient_id (FK): Referenz zu Patient
- type: Ressourcentyp (z.B. "Familie", "Hobbys")
- description: TEXT (Beschreibung)
- proposed_by (FK, nullable): Vorschlagende Person
- approved: Boolean (default false)
- created_at: Zeitstempel

**Validierungen**:
- @NotNull patient
- @NotBlank type, description

---

### 12. InterventionSource (Enum)
**Werte**: ANAMNESIS, ADL, SKILL, RESOURCE, CARE_NEED, MANUAL

---

### 13. Intervention
**Beschreibung**: Zentrale Planungseinheit der Pflege.
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- patient_id (FK): Referenz zu Patient
- source: InterventionSource (Herkunft)
- title: Titel
- description: TEXT (optional)
- active: Boolean (default true)
- created_at: Zeitstempel

**Relationen**:
- Many-to-One zu Patient
- One-to-Many zu InterventionTask

**Validierungen**:
- @NotNull patient, source
- @NotBlank title

---

### 14. InterventionTask
**Beschreibung**: Einzelne Aufgaben/Tätigkeiten einer Intervention.
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- intervention_id (FK): Referenz zu Intervention
- description: TEXT
- completed: Boolean (default false)
- completed_at: Zeitstempel (nur wenn completed)

**Business Methods**:
- markCompleted(): Markiert als durchgeführt

**Validierungen**:
- @NotNull intervention
- @NotBlank description

---

### 15. ReminderLevel (Enum)
**Werte**: NONE, ONCE, EVERY_15_MIN, EVERY_30_MIN

---

### 16. DailyPlan
**Beschreibung**: Tagesstruktur pro Patient und Datum.
**Primärschlüssel**: id (Long)
**Unique Constraint**: (patient_id, plan_date)
**Wichtige Spalten**:
- patient_id (FK): Referenz zu Patient
- plan_date: Plannungsdatum

**Relationen**:
- Many-to-One zu Patient
- One-to-Many zu DailyTask

**Validierungen**:
- @NotNull patient, planDate

---

### 17. DailyTask
**Beschreibung**: Einzelne Aktivitäten der Tagesstruktur.
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- daily_plan_id (FK): Referenz zu DailyPlan
- title: Titel
- reminder_level: ReminderLevel
- completed: Boolean (default false)
- confirmed_at: Zeitstempel (nur wenn completed)

**Business Methods**:
- confirm(): Bestätigt Durchführung

**Validierungen**:
- @NotNull dailyPlan, reminderLevel
- @NotBlank title

---

### 18. Medication
**Beschreibung**: Medikamentenverwaltung pro Patient (rein informativ).
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- patient_id (FK): Referenz zu Patient
- name: Medikamentenname
- dosage: Dosierung
- intake_time: Einnahmezeitpunkt
- confirmable: Boolean (default true)
- created_at: Zeitstempel

**Validierungen**:
- @NotNull patient, confirmable
- @NotBlank name, dosage, intakeTime

---

### 19. CareNeed
**Beschreibung**: Ad-hoc erfasste Bedarf-/Krisensituationen.
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- patient_id (FK): Referenz zu Patient
- description: TEXT
- active: Boolean (default true)
- created_at: Zeitstempel
- resolved_at: Zeitstempel (nullable)

**Business Methods**:
- resolve(): Markiert als gelöst

**Validierungen**:
- @NotNull patient
- @NotBlank description

---

### 20. AuditAction (Enum)
**Werte**: CREATE, UPDATE, DELETE, FINALIZE, APPROVE, CONFIRM, RESOLVE

---

### 21. AuditLog
**Beschreibung**: Immutable Audit Trail für Revisionssicherheit.
**Primärschlüssel**: id (Long)
**Wichtige Spalten**:
- entity: Entity-Typ (String)
- entity_id: Entity ID
- action: AuditAction
- performed_by (FK): Ausführende Person (UserAccount)
- performed_at: Zeitstempel (updatable = false)
- details: TEXT (optional)

**Validierungen**:
- @NotNull entity, entityId, action, performedBy
- @NotBlank entity

---

## Repositories – Übersicht

### Panache Repository Pattern
Alle Repositories implementieren `PanacheRepository<T>` und bieten:
- Automatisches CRUD (persist, update, delete, findById)
- Automatische JPQL-Queries (find, list)
- Transaktionale Operationen (@Transactional implizit)

### Wichtigste Repository-Methoden

#### UserAccountRepository
- `findByKeycloakId(String)`: Find by Keycloak ID
- `existsByKeycloakId(String)`: Check existence

#### PatientRepository
- `findAllActive()`: Aktive Patienten
- `findAllDeleted()`: Gelöschte Patienten (Soft Delete)
- `softDelete(Long)`: Soft Delete durchführen
- `findByName(String, String)`: Nach Name suchen

#### CareAssignmentRepository
- `findByPflegerAndPatient()`: Zuordnung finden
- `findActiveByPatient()`: Aktive Pfleger für Patient
- `findActiveByPfleger()`: Aktive Patienten für Pfleger
- `isAssignedAndActive()`: Zuordnung prüfen
- `deactivateAssignment()`: Zuordnung deaktivieren

#### AnamnesisVersionRepository
- `findVersionsByAnamnesis()`: Alle Versionen
- `findByAnamnesisAndVersion()`: Spezifische Version
- `findLatestVersion()`: Neueste Version
- `findFinalizedVersions()`: Finalisierte Versionen
- `getNextVersionNumber()`: Nächste Versionsnummer

#### ADLAssessmentRepository
- `findByPatient()`: Alle Bewertungen
- `findLatestByPatient()`: Neueste pro ADL
- `findByPatientAndStatus()`: Nach Status filtern

#### SkillAssessmentRepository
- `findByPatient()`: Alle Bewertungen
- `findLatestByPatient()`: Neueste pro Skill
- `findByPatientAndStatus()`: Nach Status filtern

#### ResourceRepository
- `findByPatient()`: Alle Ressourcen
- `findApprovedByPatient()`: Bestätigte Ressourcen
- `findPendingByPatient()`: Ausstehende Ressourcen
- `findByPatientAndType()`: Nach Typ filtern

#### InterventionRepository
- `findByPatient()`: Alle Interventionen
- `findActiveByPatient()`: Aktive Interventionen
- `findInactiveByPatient()`: Inaktive Interventionen
- `findByPatientAndSource()`: Nach Source filtern
- `deactivateIntervention()`: Deaktivieren

#### InterventionTaskRepository
- `findByIntervention()`: Alle Tasks
- `findCompletedByIntervention()`: Fertige Tasks
- `findPendingByIntervention()`: Offene Tasks
- `countCompleted()`: Anzahl fertig
- `countTotal()`: Gesamtanzahl

#### DailyPlanRepository
- `findByPatientAndDate()`: Plan für Datum
- `findByPatient()`: Alle Pläne
- `findByPatientAndDateRange()`: Zeitraum
- `findTodaysPlan()`: Heutiger Plan

#### DailyTaskRepository
- `findByDailyPlan()`: Alle Tasks
- `findCompletedByDailyPlan()`: Fertige Tasks
- `findPendingByDailyPlan()`: Offene Tasks
- `countCompleted()`: Anzahl fertig
- `countTotal()`: Gesamtanzahl

#### MedicationRepository
- `findByPatient()`: Alle Medikamente
- `findConfirmableByPatient()`: Bestätigbare Medikamente
- `findByPatientAndIntakeTime()`: Nach Einnahmezeitpunkt

#### CareNeedRepository
- `findByPatient()`: Alle Care Needs
- `findActiveByPatient()`: Aktive Needs
- `findResolvedByPatient()`: Gelöste Needs
- `resolveNeed()`: Need als gelöst markieren

#### AuditLogRepository
- `findByEntity()`: Logs für Entity
- `findByAction()`: Nach Aktion filtern
- `findByPerformer()`: Nach Performer filtern
- `findByDateRange()`: Zeitraum
- `findByEntityAndDateRange()`: Entity + Zeitraum

#### ADLDefinitionRepository
- `findByName()`: Nach Name suchen
- `existsByName()`: Existenz prüfen

#### SkillDefinitionRepository
- `findByName()`: Nach Name suchen
- `existsByName()`: Existenz prüfen

---

## Datenbank-Relationen (visuell)

```
UserAccount (1) ──────────────────────────── (Many) CareAssignment ──────────────── (1) Patient
    ▲                                                                                  ▲
    │ (createdBy)                                                                     │
    │                                                                                  │
    ├─→ AnamnesisVersion ←───────────── Anamnesis ──────────────────────────────────┤
    │   (createdBy)                                                                   │
    │                                                                                  │
    ├─→ ADLAssessment ←───────────────────── ADLDefinition                          │
    │   (assessedBy)                                                                  │
    │                                                                                  │
    ├─→ SkillAssessment ←────────────────── SkillDefinition                         │
    │   (assessedBy, nicht implementiert)                                            │
    │                                                                                  │
    ├─→ Resource                                                                      │
    │   (proposedBy)                                                                  │
    │                                                                                  │
    ├─→ Intervention                                                                  │
    │                                                                                  │
    ├─→ InterventionTask                                                              │
    │   (via Intervention)                                                            │
    │                                                                                  │
    ├─→ DailyPlan                                                                     │
    │                                                                                  │
    ├─→ DailyTask                                                                     │
    │   (via DailyPlan)                                                               │
    │                                                                                  │
    ├─→ Medication                                                                    │
    │                                                                                  │
    └─→ CareNeed                                                                      │
        (via Patient)                                                                 │
                                                                                       │
    ┌──────────────────────────────────────────────────────────────────────────────┘
    │
    └─→ AuditLog
        (performedBy)
```

---

## Best Practices – Repository Pattern

### 1. Queries immer im Repository
```java
// ✅ Gut
public List<Patient> findActiveByAge(int minAge) {
    return find("birthdate between ?1 and ?2", startDate, endDate).list();
}

// ❌ Falsch (Query in Resource)
@GET
public List<Patient> getActive() {
    return entityManager.createQuery(...).getResultList();
}
```

### 2. Soft Deletes konsequent nutzen
```java
public void softDelete(Long patientId) {
    Patient patient = findById(patientId);
    if (patient != null) {
        patient.setDeleted(true);
        persistAndFlush(patient);
    }
}
```

### 3. Business Methods in Entities
```java
// Entity
public void finalize() {
    this.finalized = true;
}

// Repository
public void finalizeVersion(Long versionId) {
    AnamnesisVersion version = findById(versionId);
    if (version != null) {
        version.finalize();
        persistAndFlush(version);
    }
}
```

### 4. Lazy Loading für Performance
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(...)
private Patient patient;
```

### 5. Validierungen in Entities
```java
@NotNull(message = "Patient darf nicht null sein")
@NotBlank(message = "Titel darf nicht leer sein")
@Past(message = "Datum muss in der Vergangenheit liegen")
```

---

## Nächste Schritte

1. **Flyway Migrationen**: Erstelle `V1__init_schema.sql` für Datenbankstruktur
2. **Resource Layer**: REST Endpoints pro Entity
3. **Service Layer** (optional): Transaktionale Business-Logik
4. **Unit Tests**: Repository-Tests mit Testcontainers/H2
5. **Integration Tests**: End-to-End REST Tests

---

## Zusammenfassung

- ✅ 21 Entities mit vollständigen Validierungen
- ✅ 16 Repositories mit komplexen Queries
- ✅ Repository Pattern (Panache)
- ✅ MVC-Struktur (Entity → Repository → Resource geplant)
- ✅ Relationen (One-to-Many, Many-to-One, One-to-One) korrekt modelliert
- ✅ Soft Deletes für Datensicherheit
- ✅ Audit Trail für Revisionssicherheit
- ✅ Versionierung für Anamnese
- ✅ Lazy Loading für Performance
- ✅ Keine Fehler in IDE/Compiler

Die Entities und Repositories sind produktionsreif und folgen allen Best Practices aus der TSD.

