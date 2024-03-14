package org.acme.resources;


import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.services.KpiService;

import static org.acme.common.Constant.APPSMS;

@Path("kpi")
@ApplicationScoped
public class KpiResource {
    @Inject
    KpiService kpiService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed({APPSMS})
    @Path("cron")
    public Response getDwh() {
        return kpiService.getDwh();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    public Response getAll() {
        return kpiService.getAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    @Path("zone")
    public Response getZone() {
        return kpiService.getZone();
    }
}
