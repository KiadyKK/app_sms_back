package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.dto.RdzDto;
import org.acme.entities.Rdz;
import org.acme.entities.User;
import org.acme.entities.Zone;
import org.acme.mappers.RdzMapper;
import org.acme.models.requests.AddRdzReq;
import org.acme.repository.RdzRepo;
import org.acme.repository.ZoneRepo;
import org.jboss.logging.Logger;
import org.mapstruct.factory.Mappers;

import java.util.List;

@ApplicationScoped
public class RdzService {
    @Inject
    Logger LOGGER;

    private final RdzMapper rdzMapper = Mappers.getMapper(RdzMapper.class);

    @Inject
    RdzRepo rdzRepo;

    @Inject
    ZoneRepo zoneRepo;

    public Response addRdz(AddRdzReq req) {
        Zone zone = zoneRepo.findById(req.getZone());

        Rdz rdz = new Rdz(req, zone);
        rdzRepo.persist(rdz);

        RdzDto rdzDto = rdzMapper.entityToDto(rdz);
        return Response.ok(rdzDto).build();
    }

    public Response getAll(String nom) {
        List<Rdz> rdzs = rdzRepo.getAll(nom);
        List<RdzDto> rdzDtos = rdzs.stream().map(rdzMapper::entityToDto).toList();
        return Response.ok(rdzDtos).build();
    }

    public Response delete(long id) {
        rdzRepo.remove(id);
        return Response.noContent().build();
    }
}
