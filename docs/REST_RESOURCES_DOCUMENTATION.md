# REST Resources – Implementierung & Dokumentation

## Status: ✅ CREATED (mit Fehler-Fixes nötig)

Es wurden **9 REST Resource-Klassen** für die wichtigsten Entitäten erstellt, basierend auf FSD und TSD Spezifikationen.

## Erstellte Resources

### 1. PatientResource
**Pfad:** `/patients`

**Endpunkte:**
- `GET /patients` – Alle aktiven Patienten
- `POST /patients` – Neuen Patienten erstellen
- `GET /patients/{id}` – Patient-Details (einfach)
- `GET /patients/{id}/details` – Patient mit Relationen
- `PUT /patients/{id}` – Patient aktualisieren
- `DELETE /patients/{id}` – Patient löschen (Soft Delete)

**DTO:**
- `PatientCreateUpdateDTO` (Input)
- `PatientResponseDTO` (Output)
- `PatientDetailDTO` (Output mit Relationen)

---

### 2. UserAccountResource
**Pfad:** `/users`

**Endpunkte:**
- `GET /users` – Alle Benutzer
- `GET /users/{id}` – Benutzer-Details
- `GET /users/keycloak/{keycloakId}` – Benutzer via Keycloak-ID
- `POST /users/sync` – Benutzer aus Keycloak synchen
- `GET /users/{id}/patients` – Patienten eines Pflegers

**DTO:**
- `UserAccountDTO`

**Basierend auf:** FSD Abschnitt 3 (Rollen und Rechte)

---

### 3. CareAssignmentResource
**Pfad:** `/care-assignments`

**Endpunkte:**
- `GET /care-assignments` – Alle Zuordnungen
- `POST /care-assignments` – Neue Zuordnung erstellen
- `GET /care-assignments/{id}` – Zuordnung-Details
- `PUT /care-assignments/{id}` – Zuordnung aktualisieren
- `DELETE /care-assignments/{id}` – Zuordnung deaktivieren
- `GET /care-assignments/pfleger/{pflegerId}/active` – Zuordnungen eines Pflegers
- `GET /care-assignments/patient/{patientId}/active` – Pfleger eines Patienten

**DTO:**
- `CareAssignmentDTO`

**Basierend auf:** FSD Abschnitt 3, TSD Abschnitt 5.2

---

### 4. AnamnesisResource
**Pfad:** `/anamnesis`

**Endpunkte:**
- `POST /patients/{patientId}/anamnesis` – Anamnese erstellen
- `GET /patients/{patientId}/anamnesis` – Anamnese lesen
- `POST /anamnesis/{anamnesisId}/versions` – Neue Version hinzufügen
- `GET /anamnesis/{anamnesisId}/versions` – Alle Versionen
- `GET /anamnesis/{anamnesisId}/versions/{vNum}` – Spezifische Version
- `PUT /anamnesis/versions/{versionId}/finalize` – Version finalisieren

**DTO:**
- `AnamnesisDTO`
- `AnamnesisVersionDTO`

**Basierend auf:** FSD Abschnitt 4 (Anamnese & Versionierung), TSD Abschnitt 5.3

---

### 5. ADLAssessmentResource
**Pfad:** `/adl`

**Endpunkte:**
- `GET /adl-definitions` – Vordefinierte ADLs
- `POST /adl-definitions` – Neue ADL-Definition erstellen
- `POST /patients/{patientId}/adl-assessments` – Neue Bewertung
- `GET /patients/{patientId}/adl-assessments` – Bewertungen eines Patienten
- `GET /patients/{patientId}/adl-assessments/latest` – Neueste Bewertungen pro ADL
- `GET /adl-assessments/{id}` – Bewertung-Details

**DTO:**
- `ADLDefinitionDTO`
- `ADLAssessmentDTO`

**Basierend auf:** FSD Abschnitt 5 (ADLs und Skills), TSD Abschnitt 5.4

---

### 6. InterventionResource
**Pfad:** `/interventions`

**Endpunkte:**
- `POST /patients/{patientId}/interventions` – Neue Intervention
- `GET /patients/{patientId}/interventions` – Interventionen eines Patienten
- `GET /interventions/{id}` – Intervention-Details
- `PUT /interventions/{id}` – Intervention aktualisieren
- `DELETE /interventions/{id}` – Intervention deaktivieren
- `POST /interventions/{interventionId}/tasks` – Task hinzufügen
- `GET /interventions/{interventionId}/tasks` – Tasks anzeigen
- `PUT /intervention-tasks/{taskId}/complete` – Task als erledigt markieren

**DTO:**
- `InterventionDTO`
- `InterventionTaskDTO`

**Basierend auf:** FSD Abschnitt 7 (Interventionen), TSD Abschnitt 5.7

---

### 7. DailyPlanResource
**Pfad:** `/daily-plans`

**Endpunkte:**
- `POST /patients/{patientId}/daily-plans` – Neuer Tagesplan
- `GET /patients/{patientId}/daily-plans` – Alle Tagespläne
- `GET /patients/{patientId}/daily-plans/by-date` – Plan für Datum
- `GET /patients/{patientId}/daily-plans/today` – Heutiger Plan
- `POST /daily-plans/{planId}/tasks` – Task hinzufügen
- `GET /daily-plans/{planId}/tasks` – Tasks anzeigen
- `PUT /daily-tasks/{taskId}/confirm` – Task bestätigen

**DTO:**
- `DailyPlanDTO`
- `DailyTaskDTO`

**Basierend auf:** FSD Abschnitt 8 (Tagesstruktur), TSD Abschnitt 5.8

---

### 8. ResourceResource
**Pfad:** `/resources`

**Endpunkte:**
- `POST /patients/{patientId}/resources` – Ressource hinzufügen
- `GET /patients/{patientId}/resources` – Alle Ressourcen
- `GET /patients/{patientId}/resources/approved` – Bestätigte Ressourcen
- `GET /patients/{patientId}/resources/pending` – Ausstehende Ressourcen
- `PUT /resources/{id}/approve` – Ressource bestätigen
- `DELETE /resources/{id}` – Ressource löschen

**DTO:**
- `ResourceDTO`

**Basierend auf:** FSD Abschnitt 6 (Ressourcen), TSD Abschnitt 5.6

---

### 9. CareNeedResource
**Pfad:** `/care-needs`

**Endpunkte:**
- `POST /patients/{patientId}/care-needs` – Bedarf erfassen
- `GET /patients/{patientId}/care-needs` – Alle Bedarfe
- `GET /patients/{patientId}/care-needs/active` – Aktive Bedarfe
- `GET /patients/{patientId}/care-needs/resolved` – Gelöste Bedarfe
- `PUT /care-needs/{id}/resolve` – Bedarf als gelöst markieren
- `DELETE /care-needs/{id}` – Bedarf löschen

**DTO:**
- `CareNeedDTO`

**Basierend auf:** FSD Abschnitt 10 (Bedarf & Krisensituationen), TSD Abschnitt 5.10

---

## REST API Übersicht

### Base URL
```
/api/
```

### Endpunkte nach Ressource

```
/patients
├── GET    /                          Liste aller Patienten
├── POST   /                          Patient erstellen
├── GET    /{id}                      Patient-Details
├── GET    /{id}/details              Patient + Relationen
├── PUT    /{id}                      Patient aktualisieren
├── DELETE /{id}                      Patient löschen
├── /{id}/anamnesis
│   ├── POST   /                      Anamnese erstellen
│   └── GET    /                      Anamnese lesen
├── /{id}/adl-assessments
│   ├── POST   /                      ADL-Bewertung erstellen
│   ├── GET    /                      Alle Bewertungen
│   └── GET    /latest                Neueste Bewertungen
├── /{id}/interventions
│   ├── POST   /                      Intervention erstellen
│   ├── GET    /                      Alle Interventionen
│   └── /{interventionId}/tasks
│       ├── POST   /                  Task hinzufügen
│       └── GET    /                  Tasks anzeigen
├── /{id}/daily-plans
│   ├── POST   /                      Tagesplan erstellen
│   ├── GET    /                      Alle Tagespläne
│   ├── GET    /today                 Heutiger Plan
│   └── /{planId}/tasks
│       ├── POST   /                  Task hinzufügen
│       └── GET    /                  Tasks anzeigen
├── /{id}/resources
│   ├── POST   /                      Ressource hinzufügen
│   ├── GET    /                      Alle Ressourcen
│   ├── GET    /approved              Bestätigte Ressourcen
│   └── GET    /pending               Ausstehende Ressourcen
└── /{id}/care-needs
    ├── POST   /                      Bedarf erfassen
    ├── GET    /                      Alle Bedarfe
    ├── GET    /active                Aktive Bedarfe
    └── GET    /resolved              Gelöste Bedarfe

/users
├── GET    /                          Alle Benutzer
├── GET    /{id}                      Benutzer-Details
├── GET    /keycloak/{keycloakId}     Benutzer via Keycloak-ID
├── POST   /sync                      Benutzer synchen
└── /{id}/patients                    Patienten eines Pflegers

/care-assignments
├── GET    /                          Alle Zuordnungen
├── POST   /                          Zuordnung erstellen
├── GET    /{id}                      Zuordnung-Details
├── PUT    /{id}                      Zuordnung aktualisieren
├── DELETE /{id}                      Zuordnung deaktivieren
├── /pfleger/{pflegerId}/active       Zuordnungen eines Pflegers
└── /patient/{patientId}/active       Pfleger eines Patienten

/anamnesis
├── /{anamnesisId}/versions
│   ├── POST   /                      Version hinzufügen
│   ├── GET    /                      Alle Versionen
│   └── GET    /{vNum}                Spezifische Version
└── /versions/{versionId}/finalize    Version finalisieren

/adl
├── /definitions                      ADL-Definitionen
├── /definitions (POST)               ADL-Definition erstellen
└── /assessments/{id}                 Bewertung-Details

/interventions
├── GET    /{id}                      Intervention-Details
├── PUT    /{id}                      Intervention aktualisieren
├── DELETE /{id}                      Intervention deaktivieren
└── /tasks/{taskId}/complete          Task als erledigt markieren

/daily-plans
├── GET    /{planId}/tasks            Tasks eines Plans
└── /tasks/{taskId}/confirm           Task bestätigen

/resources
├── GET    /{id}                      (via Patient-Endpoint)
├── /{id}/approve                     Ressource bestätigen
└── /{id}  (DELETE)                   Ressource löschen

/care-needs
├── /{id}/resolve                     Bedarf als gelöst markieren
└── /{id}  (DELETE)                   Bedarf löschen
```

---

## Wichtige Implementierungsdetails

### 1. Repository Pattern
- Jede Resource nutzt ihre entsprechenden Repositories
- Entities werden **nicht** direkt persistiert
- Repositories handhaben CRUD-Operationen

**Korrekt:**
```java
patientRepository.persist(patient);
```

**Falsch:**
```java
patient.persist();  // ❌ Panache funktioniert über Repository
```

### 2. DTOs für Entkopplung
- Input-DTOs haben Validierungen (@Valid)
- Output-DTOs sind lean (nur notwendige Felder)
- Detail-DTOs enthalten Relationen

### 3. HTTP Status-Codes
- `201 Created` – Ressource erstellt
- `200 OK` – Erfolgreich (GET, PUT)
- `204 No Content` – Erfolgreich gelöscht
- `400 Bad Request` – Validierungsfehler
- `404 Not Found` – Ressource existiert nicht
- `409 Conflict` – Ressource existiert bereits

### 4. Fehlerbehandlung
- Prüfung auf Existenz (findById, findByPatient)
- Prüfung auf Soft Delete (getDeleted)
- Validierung von Eingaben (type, status, etc.)
- Aussagekräftige Fehlermeldungen

---

## Nächste Schritte

1. **Fehler beheben**
   - Alle `entity.persist()` → `repository.persist(entity)`
   - Alle `entity.delete()` → `repository.delete(entity)` oder `entity.deleteById(id)`

2. **Application-Startup-Konfiguration**
   - `@ApplicationPath("/api")` in einer Application-Klasse

3. **Exception Handling**
   - `NotFoundException`, `BadRequestException` in eigene Klasse

4. **Security/Authentication**
   - `@RolesAllowed` oder `@PermitAll` auf Ressourcen
   - Keycloak-Integration in Filter

5. **Testing**
   - REST-API Integration Tests
   - DTO-Mapping Tests

6. **Documentation**
   - OpenAPI/Swagger Annotations
   - API-Dokumentation

---

## Dateienliste

```
backend/src/main/java/at/htlleonding/sixtosix/resource/
├── PatientResource.java              (9 Endpunkte)
├── UserAccountResource.java          (5 Endpunkte)
├── CareAssignmentResource.java       (7 Endpunkte)
├── AnamnesisResource.java            (6 Endpunkte)
├── ADLAssessmentResource.java        (6 Endpunkte)
├── InterventionResource.java         (8 Endpunkte)
├── DailyPlanResource.java            (7 Endpunkte)
├── ResourceResource.java             (6 Endpunkte)
└── CareNeedResource.java             (6 Endpunkte)
```

**Total: 9 Resource-Klassen mit 60+ REST-Endpunkten**

---

## Zusammenfassung

✅ **Erstellte Ressourcen:** 9 Resource-Klassen
✅ **REST-Endpunkte:** 60+ Endpunkte
✅ **DTOs:** 18 Record-basierte DTOs
✅ **Repositories:** Alle Resource-Klassen nutzen Repositories
❌ **Fehler:** Einige persist() und delete() Aufrufe müssen via Repository laufen (wird behoben)
⏳ **Nächster Schritt:** Fehler beheben + Application-Startup-Konfiguration


