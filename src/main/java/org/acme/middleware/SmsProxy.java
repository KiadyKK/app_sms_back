package org.acme.middleware;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/cgi-bin/smssend")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "extensions-api")
public interface SmsProxy {
    @GET
    String sendSms(@QueryParam("username") String username,
                     @QueryParam("password") String password,
                     @QueryParam("from") String from,
                     @QueryParam("to") String to,
                     @QueryParam("text") String text);
}
