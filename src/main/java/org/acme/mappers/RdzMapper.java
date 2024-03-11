package org.acme.mappers;

import org.acme.dto.RdzDto;
import org.acme.entities.Rdz;
import org.mapstruct.Mapper;

@Mapper
public interface RdzMapper {
    RdzDto entityToDto(Rdz rdz);
}
