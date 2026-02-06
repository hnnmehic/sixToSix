package at.htlleonding.sixtosix.service;

import at.htlleonding.sixtosix.dto.DoctorDTO;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Einfacher DoctorService, der eine statische Liste fiktiver Ärzte zurückgibt.
 * Nützlich für Frontend-Tests ohne Google Maps Integration.
 */
@ApplicationScoped
public class DoctorService {

    /**
     * Gibt eine statische Liste mit drei fiktiven Ärzten zurück (Standard: Wien)
     *
     * @param city optional, wenn null oder leer → "Wien"
     * @return Liste von DoctorDTO
     */
    public List<DoctorDTO> findNearbyDoctors(String city) {
        String c = (city == null || city.isBlank()) ? "Wien" : city;

        return List.of(
                new DoctorDTO(1L, "Dr. Markus Huber", "Schottenring 20, 1010 Wien", "+43 1 2345678", c),
                new DoctorDTO(2L, "Dr.in Anna Mayer", "Lazarettgasse 13, 1090 Wien", "+43 1 8765432", c),
                new DoctorDTO(3L, "Dr. Peter Gruber", "Favoritenstraße 100, 1100 Wien", "+43 1 9988776", c)
        );
    }
}

