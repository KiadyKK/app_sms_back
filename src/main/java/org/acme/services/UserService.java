package org.acme.services;
import io.smallrye.jwt.build.Jwt;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.model.app_sms_833.User;
import org.acme.repo.app_sms_833.UserRepo;
import org.acme.requests.AddUserReq;
import org.acme.requests.LoginReq;
import org.acme.requests.PutPasswordReq;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

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
        try{
            String tri = req.getTri();
            String mdp = req.getMdp();
            User user = userRepo.findByTri(tri);
            Optional<User> optional = Optional.ofNullable(user);
            if (optional.isEmpty())
                return Response.status(Response.Status.NOT_FOUND).entity("User Not found. Contact DSI support").build();
            if (tri.equals(ADMIN_TRI) && mdp.equals(ADMIN_MDP)) {
                return generateJwt(user);
            } else {
                boolean checkMdp = BCrypt.checkpw(req.getMdp(), user.getMdp());
                if (!checkMdp) {
                    return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid Password !").build();
                } else {
                    return generateJwt(user);
                }
            }
        }catch (Exception e){
            return Response.serverError().build();
        }
    }
    public boolean modifyMdp(@NotNull PutPasswordReq req){
        String trigramme=req.getTrigramme();
        String newMdp=req.getNewPassword();
        try{
            User user=userRepo.findByTri(trigramme);
            boolean checkPassword=BCrypt.checkpw(req.getPassword(),user.getMdp());
            if(checkPassword){
                user.setMdp(BCrypt.hashpw(newMdp,BCrypt.gensalt()));

                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return  false;
        }
    }

    private Response generateJwt(User user) {
        try{
            String token = Jwt.issuer("appsms")
                    .subject("appsms")
                    .groups(APPSMS)
                    .expiresAt(System.currentTimeMillis() + 3600)
                    .sign();

            JsonObject jsonObject = new JsonObject();
            jsonObject.put("user", user).put("token", "Bearer " + token);

            LOGGER.info("User logged successfully");
            return Response.ok(jsonObject).build();
        }catch (Exception e){
            return Response.serverError().build();
        }
    }

    public Response addUser(AddUserReq req) {
        try{
            User user = new User(req);
            userRepo.persist(user);
            return Response.ok(user).build();
        }catch(Exception e){
            return Response.serverError().build();
        }
    }

    public Response getAll(String nom) {
        try{
            List<User> users = userRepo.getAll(nom);
            return Response.ok(users).build();
        }catch (Exception e){
            return Response.serverError().build();
        }
    }

    public Response delete(long id) {
        try{
            userRepo.remove(id);
            return Response.noContent().build();
        }catch (Exception e){
            return Response.serverError().build();
        }
    }
}
