# Anleitung: Datenbank (Postgres) und Keycloak starten

Diese Datei erklärt, wie du die in diesem Projekt konfigurierte PostgreSQL-Datenbank und Keycloak lokal startest.

Wichtig: Die Compose-Datei liegt unter `compose/docker-compose.yml`.

Voraussetzungen
- Docker und Docker Compose installiert.
- Eine Kopie der Beispiel-Umgebungsdatei `.env.example` als `.env` im Projekt-Root (nicht ins VCS!).

Vorbereitung
1. Kopiere die Beispiel-Umgebungsdatei und passe Passwörter an:

```powershell
Copy-Item .\.env.example .\.env
notepad .\.env
```

2. Stelle sicher, dass in `.env` die folgenden Variablen gesetzt sind:
- POSTGRES_DB
- POSTGRES_USER
- POSTGRES_PASSWORD
- KEYCLOAK_ADMIN
- KEYCLOAK_ADMIN_PASSWORD

Starten (empfohlen aus dem Projekt-Root)

Option A — Starten mit Angabe der Compose-Datei:

```powershell
# Aus dem Projekt-Root
docker compose -f .\compose\docker-compose.yml up -d
```

Option B — Starten aus dem `compose`-Verzeichnis (env-file beachten):

```powershell
# In das compose-Verzeichnis wechseln
Push-Location .\compose
# Wenn die .env im Projekt-Root liegt, explizit angeben:
docker compose --env-file ..\.env up -d
Pop-Location
```

Prüfen, ob die Dienste laufen

```powershell
# Alle Container anzeigen
docker compose -f .\compose\docker-compose.yml ps

# Logs von Keycloak live verfolgen (Fehler beim DB-Start sichtbar)
docker compose -f .\compose\docker-compose.yml logs -f keycloak

# Logs von Postgres
docker compose -f .\compose\docker-compose.yml logs -f postgres
```

Endpunkte & Zugang
- Keycloak-Admin-Console: http://localhost:8080 (Benutzer/Passwort aus `.env`)
- PostgreSQL: localhost:5432 (Zugangsdaten aus `.env`)

Stoppen & Aufräumen

```powershell
# Stoppen
docker compose -f .\compose\docker-compose.yml stop

# Entfernen (Container + Netzwerk, Volumes bleiben erhalten)
docker compose -f .\compose\docker-compose.yml down

# Entfernen inkl. benannter Volumes (Achtung: Datenverlust)
docker compose -f .\compose\docker-compose.yml down --volumes
```

Troubleshooting
- Keycloak meldet Verbindungsfehler zur DB: prüfe dass Postgres läuft und `.env`-Variablen korrekt sind.
- Wenn Healthchecks fehlschlagen, warte ein paar Sekunden und prüfe `docker compose logs`.
- Falls du die Compose-Datei verschiebst, achte auf relative Pfade und das `--env-file`-Flag.

Sicherheit
- Speichere keine echten Passwörter im Repository. Nutze `.env` (in `.gitignore`) oder Docker Secrets für produktive Setups.

Herkunft
- Diese Anleitung ersetzt die frühere `compose/README.md`, die nun auf diese Datei verweist.

Wenn du möchtest, füge ich diese Anleitung zusätzlich als Abschnitt in `README.adoc` ein oder entferne die alte `compose/README.md` vollständig.

