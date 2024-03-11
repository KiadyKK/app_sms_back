package org.acme.mappers;

import org.acme.dto.ZoneDto;
import org.acme.entities.Zone;
import org.mapstruct.Mapper;

@Mapper
public interface ZoneMapper {
    ZoneDto entityToDto(Zone zone);
}
