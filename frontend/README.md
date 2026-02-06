# sixToSix - Mobile Frontend (Expo + React Native + TypeScript)

Dieses Verzeichnis enthält ein Expo-basiertes React Native Projekt (TypeScript).

Wichtig: Dieses Template wurde minimal erzeugt; installiere Abhängigkeiten bevor du startest.

Voraussetzungen
- Node.js (>=18)
- npm oder yarn
- Expo CLI (optional)

Installieren

```bash
cd frontend
npm install
# oder
# yarn install
```

Starten (Entwicklung)

```bash
# Starte Expo
npm run start
# öffnet Metro und Expo Devtools
```

Erster Screen
- `PatientListScreen` ruft die Backend-API an: `http://localhost:8080/patients` (falls dein Backend `/api/patients` benutzt, passe `src/api/index.ts` oder die Anfrage in `PatientListScreen.tsx` an).

Hinweis
- Wenn du Backend und Mobile-App gleichzeitig laufen lässt, achte auf CORS / Netzwerk (Android-Emulator verwendet oft `10.0.2.2` statt `localhost`).
- Wenn Backend die Endpoints unter `/api` exposed, benutze `http://localhost:8080/api/patients`.

