package org.acme.services;

import io.smallrye.jwt.build.Jwt;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.entities.User;
import org.acme.models.requests.AddUserReq;
import org.acme.models.requests.LoginReq;
import org.acme.repository.UserRepo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;

import static org.acme.common.Constant.APPSMS;

@ApplicationScoped
public class UserService {
    @Inject
    Logger LOGGER;

    @ConfigProperty(name = "admin.tri")
    private String ADMIN_TRI;

    @ConfigProperty(name = "admin.mdp")
    private String ADMIN_MDP;

    @Inject
    UserRepo userRepo;

    public Response login(LoginReq req) {
        String tri = req.getTri();
        String mdp = req.getMdp();

        User user = userRepo.findByTri(tri);
        Optional<User> optional = Optional.ofNullable(user);
        if (optional.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).entity("User Not found. Contact DSI support").build();

        if (tri.equals(ADMIN_TRI) && mdp.equals(ADMIN_MDP)) {
            return generateJwt(user);
        }

        return Response.noContent().build();
    }

    private Response generateJwt(User user) {
        String token = Jwt.issuer("appsms")
                .subject("appsms")
                .groups(APPSMS)
                .expiresAt(System.currentTimeMillis() + 3600)
                .sign();

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("user", user).put("token", "Bearer " + token);

        LOGGER.info("User logged successfully");
        return Response.ok(jsonObject).build();
    }

    public Response addUser(AddUserReq req) {
        User user = new User(req);
        userRepo.persist(user);

        return Response.ok(user).build();
    }

    public Response getAll(String nom) {
        List<User> users = userRepo.getAll(nom);
        return Response.ok(users).build();
    }

    public Response delete(long id) {
        userRepo.remove(id);
        return Response.noContent().build();
    }
}
