package org.acme.resources;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.requests.AddRdzReq;
import org.acme.services.RdzService;
import static org.acme.common.Constant.APPSMS;
@Path("rdz")
@ApplicationScoped
public class RdzResource {
    @Inject
    RdzService rdzService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addRdz(AddRdzReq req) {
        return rdzService.addRdz(req);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    public Response getAll(@QueryParam("nom") String nom) {
        return rdzService.getAll(nom);
    }


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    @Transactional
    @Path("{id}")
    public Response remove(@PathParam("id") long id) {
        return rdzService.delete(id);
    }
}
