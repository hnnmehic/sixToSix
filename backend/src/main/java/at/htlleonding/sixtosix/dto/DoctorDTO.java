package at.htlleonding.sixtosix.dto;

/**
 * DTO für fiktive Ärzte (zum Testen der Notfall-/Finder-Logik ohne Google Maps)
 */
public record DoctorDTO(
    Long id,
    String name,
    String address,
    String phone,
    String city
) {
}

