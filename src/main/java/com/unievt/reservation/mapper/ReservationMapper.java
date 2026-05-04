package com.unievt.reservation.mapper;

import com.unievt.reservation.dto.ReservationDTO;
import com.unievt.reservation.dto.ReservationResponseDTO;
import com.unievt.reservation.entity.Reservation;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "salle", ignore = true)
    @Mapping(target = "demandeur", ignore = true)
    @Mapping(target = "approbateur", ignore = true)
    Reservation toEntity(ReservationDTO dto);

    @Mapping(source = "salle.id", target = "salleId")
    @Mapping(source = "salle.nom", target = "salleNom")
    @Mapping(source = "demandeur.id", target = "demandeurId")
    @Mapping(source = "demandeur.nom", target = "demandeurNom")
    @Mapping(source = "demandeur.prenom", target = "demandeurPrenom")
    @Mapping(source = "approbateur.id", target = "approbateurId")
    @Mapping(source = "approbateur.nom", target = "approbateurNom")
    ReservationResponseDTO toResponseDTO(Reservation entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "salle", ignore = true)
    @Mapping(target = "demandeur", ignore = true)
    @Mapping(target = "approbateur", ignore = true)
    void updateEntityFromDTO(ReservationDTO dto, @MappingTarget Reservation entity);
}
