package at.htlleonding.sixtosix.dto;

import java.time.LocalDateTime;

/**
 * DTO für DailyTask (Record)
 * Einzelne Aktivitäten der Tagesstruktur.
 * Immutable und kompakt mit Java Records.
 */
public record DailyTaskDTO(
    Long id,
    Long dailyPlanId,
    String title,
    String reminderLevel,
    Boolean completed,
    LocalDateTime confirmedAt
) {
}

