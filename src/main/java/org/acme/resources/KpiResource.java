package org.acme.resources;


import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.services.KpiService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

import static org.acme.common.Constant.APPSMS;

@Path("kpi")
@ApplicationScoped
public class KpiResource {
    @Inject
    KpiService kpiService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("cron")
    public Response getDwh() {
        return kpiService.getDwh();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("test-sms/{msisdn}")
    public Response testJson(@PathParam("msisdn") String msisdn) {
        return kpiService.testSms(msisdn);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    public Response getAll(@QueryParam("date") String date) {
        return kpiService.getAll(date);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed({APPSMS})
    @Path("dwh")
    public Response getAllDwh(@QueryParam("date") String date) {
        return kpiService.getAllDwh(date);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    @Path("zone")
    public Response getZone() {
        return kpiService.getZone();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    @Path("send")
    public Response send(@HeaderParam("tri") String tri, @QueryParam("date") String date, @QueryParam("source") String source) throws UnsupportedEncodingException {
        return kpiService.sendSms(date, source, tri);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    @Path("historic")
    public Response getHistoric(@QueryParam("date") String date) {
        return kpiService.getHistoric(date);
    }
}
