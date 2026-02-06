package at.htlleonding.sixtosix.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO für DailyPlan (Record)
 * Tagesstruktur pro Patient und Datum.
 * Immutable und kompakt mit Java Records.
 */
public record DailyPlanDTO(
    Long id,
    Long patientId,
    LocalDate planDate,
    List<DailyTaskDTO> tasks
) {
}

