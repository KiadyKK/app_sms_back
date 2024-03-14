package org.acme.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.requests.AddUserReq;
import org.acme.requests.LoginReq;
import org.acme.services.UserService;

import static org.acme.common.Constant.APPSMS;

@Path("user")
@ApplicationScoped
public class UserResource {
    @Inject
    UserService userService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    @Path("login")
    public Response signUp(LoginReq req) {
        return userService.login(req);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    @Transactional
    @Path("new")
    public Response addUser(AddUserReq req) {
        return userService.addUser(req);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    public Response getAll(@QueryParam("nom") String nom) {
        return userService.getAll(nom);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({APPSMS})
    @Transactional
    @Path("{id}")
    public Response remove(@PathParam("id") long id) {
        return userService.delete(id);
    }
}
