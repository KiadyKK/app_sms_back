package org.acme.services;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.model.app_sms_833.Rdz;
import org.acme.repo.app_sms_833.RdzRepo;
import org.acme.requests.AddRdzReq;
import org.jboss.logging.Logger;
import java.util.List;
@ApplicationScoped
public class RdzService {
    @Inject
    Logger LOGGER;
    @Inject
    RdzRepo rdzRepo;
    public Response addRdz(AddRdzReq req) {
        try{
            Rdz rdz = new Rdz(req);
            rdzRepo.persist(rdz);
            return Response.ok(rdz).build();
        }catch (Exception e){
            return Response.serverError().build();
        }
    }
    public Response getAll(String nom) {
        try{
            List<Rdz> rdzs = rdzRepo.getAll(nom);
            return Response.ok(rdzs).build();
        }catch(Exception e){
            return Response.serverError().build();
        }
    }
    public Response delete(long id) {
        try{
            rdzRepo.remove(id);
            return Response.noContent().build();
        }catch (Exception e){
            return Response.serverError().build();
        }
    }
}
