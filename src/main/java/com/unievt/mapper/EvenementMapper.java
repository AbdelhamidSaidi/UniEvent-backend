package com.unievt.mapper;

import com.unievt.dto.evenement.EvenementCreateDTO;
import com.unievt.dto.evenement.EvenementResponseDTO;
import com.unievt.dto.evenement.EvenementUpdateDTO;
import com.unievt.entity.Evenement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EvenementMapper {

    @Mapping(target = "club.id", source = "clubId")
    @Mapping(target = "organisateur.id", source = "organisateurId")
    Evenement toEntity(EvenementCreateDTO dto);

    @Mapping(target = "clubId", expression = "java(evenement.getClub()!=null? evenement.getClub().getId() : null)")
    @Mapping(target = "clubName", expression = "java(evenement.getClub()!=null? evenement.getClub().toString() : null)")
    @Mapping(target = "organisateurId", expression = "java(evenement.getOrganisateur()!=null? evenement.getOrganisateur().getId() : null)")
    @Mapping(target = "organisateurName", expression = "java(evenement.getOrganisateur()!=null? evenement.getOrganisateur().toString() : null)")
    EvenementResponseDTO toResponseDTO(Evenement evenement);

    void updateEntityFromDTO(EvenementUpdateDTO dto, @MappingTarget Evenement entity);
}
