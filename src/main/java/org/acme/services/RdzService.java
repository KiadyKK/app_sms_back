package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.mappers.RdzMapper;
import org.acme.model.app_sms_833.Rdz;
import org.acme.repo.app_sms_833.RdzRepo;
import org.acme.requests.AddRdzReq;
import org.jboss.logging.Logger;
import org.mapstruct.factory.Mappers;

import java.util.List;

@ApplicationScoped
public class RdzService {
    @Inject
    Logger LOGGER;

//    private final RdzMapper rdzMapper = Mappers.getMapper(RdzMapper.class);

    @Inject
    RdzRepo rdzRepo;

    public Response addRdz(AddRdzReq req) {
        Rdz rdz = new Rdz(req);
        rdzRepo.persist(rdz);

        return Response.ok(rdz).build();
    }

    public Response getAll(String nom) {
        List<Rdz> rdzs = rdzRepo.getAll(nom);
        return Response.ok(rdzs).build();
    }

    public Response delete(long id) {
        rdzRepo.remove(id);
        return Response.noContent().build();
    }
}
