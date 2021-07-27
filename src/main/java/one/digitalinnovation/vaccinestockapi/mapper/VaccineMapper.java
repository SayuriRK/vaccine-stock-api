package one.digitalinnovation.vaccinestockapi.mapper;

import one.digitalinnovation.vaccinestockapi.dto.VaccineDTO;
import one.digitalinnovation.vaccinestockapi.entity.Vaccine;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VaccineMapper {

    VaccineMapper INSTANCE = Mappers.getMapper(VaccineMapper.class);

    Vaccine toModel(VaccineDTO vaccineDTO);

    VaccineDTO toDTO (Vaccine vaccine);
}
