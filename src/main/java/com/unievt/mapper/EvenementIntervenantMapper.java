package com.unievt.mapper;

import com.unievt.dto.evenementintervenant.EvenementIntervenantCreateDTO;
import com.unievt.dto.evenementintervenant.EvenementIntervenantResponseDTO;
import com.unievt.entity.EvenementIntervenant;
import com.unievt.entity.EvenementIntervenantId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EvenementIntervenantMapper {

    @Mapping(target = "id", expression = "java(new com.unievt.entity.EvenementIntervenantId(dto.getEvenementId(), dto.getIntervenantId()))")
    @Mapping(target = "evenement.id", source = "evenementId")
    @Mapping(target = "intervenant.id", source = "intervenantId")
    EvenementIntervenant toEntity(EvenementIntervenantCreateDTO dto);

    @Mapping(target = "evenementId", expression = "java(entity.getEvenement()!=null? entity.getEvenement().getId(): null)")
    @Mapping(target = "intervenantId", expression = "java(entity.getIntervenant()!=null? entity.getIntervenant().getId(): null)")
    @Mapping(target = "intervenantName", expression = "java(entity.getIntervenant()!=null? entity.getIntervenant().toString(): null)")
    @Mapping(target = "evenementTitle", expression = "java(entity.getEvenement()!=null? entity.getEvenement().toString(): null)")
    EvenementIntervenantResponseDTO toResponseDTO(EvenementIntervenant entity);
}
