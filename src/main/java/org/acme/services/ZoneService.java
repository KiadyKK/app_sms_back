package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.dto.ZoneDto;
import org.acme.entities.Zone;
import org.acme.mappers.ZoneMapper;
import org.acme.repository.ZoneRepo;
import org.mapstruct.factory.Mappers;

import java.util.List;

@ApplicationScoped
public class ZoneService {
    private final ZoneMapper zoneMapper = Mappers.getMapper(ZoneMapper.class);

    @Inject
    ZoneRepo zoneRepo;

    public Response getAll(String name) {
        List<Zone> zones = zoneRepo.getAll(name);
        List<ZoneDto> zoneDtos = zones.stream().map(zoneMapper::entityToDto).toList();
        return Response.ok(zoneDtos).build();
    }
}
