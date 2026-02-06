package at.htlleonding.sixtosix.resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Application-Startup-Konfiguration für Quarkus
 * Definiert den Basis-Pfad für alle REST-Endpunkte
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // Alle @Path Klassen werden automatisch von Quarkus registriert
}

