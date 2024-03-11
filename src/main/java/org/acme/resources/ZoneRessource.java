package org.acme.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.services.ZoneService;

import static org.acme.common.Constant.APPSMS;

@Path("zone")
@ApplicationScoped
public class ZoneRessource {
    @Inject
    ZoneService zoneService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    public Response getAll(@QueryParam("name") String name) {
        return zoneService.getAll(name);
    }

}
