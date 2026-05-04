package com.unievt.salle.mapper;

import com.unievt.salle.dto.SalleDTO;
import com.unievt.salle.dto.SalleResponseDTO;
import com.unievt.salle.entity.Salle;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SalleMapper {

    Salle toEntity(SalleDTO dto);

    SalleResponseDTO toResponseDTO(Salle entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(SalleDTO dto, @MappingTarget Salle entity);
}
