package org.acme.mappers;
import org.acme.dto.RdzDto;
import org.acme.model.app_sms_833.Rdz;
import org.mapstruct.Mapper;
@Mapper
public interface RdzMapper {
    RdzDto entityToDto(Rdz rdz);
}
