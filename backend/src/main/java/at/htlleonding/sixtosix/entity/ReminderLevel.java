package at.htlleonding.sixtosix.entity;

/**
 * Enum für Reminder Level
 * Bestimmt, wie oft Patienten erinnert werden sollen.
 */
public enum ReminderLevel {
    NONE,       // Keine Erinnerung
    ONCE,       // Einmalige Erinnerung
    EVERY_15_MIN,  // Alle 15 Minuten
    EVERY_30_MIN   // Alle 30 Minuten
}

