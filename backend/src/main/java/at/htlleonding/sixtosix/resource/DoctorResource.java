package at.htlleonding.sixtosix.resource;

import at.htlleonding.sixtosix.dto.DoctorDTO;
import at.htlleonding.sixtosix.service.DoctorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Resource für das Abrufen von Ärzten (statische Liste) - Testzwecke
 */
@Path("/doctors")
@Produces(MediaType.APPLICATION_JSON)
public class DoctorResource {

    @Inject
    DoctorService doctorService;

    /**
     * GET /api/doctors/nearby?city=Wien
     * Liefert eine statische Liste von Ärzten zurück (Standort-Parameter optional).
     */
    @GET
    @Path("/nearby")
    public Response findNearbyDoctors(@QueryParam("city") String city) {
        List<DoctorDTO> doctors = doctorService.findNearbyDoctors(city);
        return Response.ok(doctors).build();
    }
}

