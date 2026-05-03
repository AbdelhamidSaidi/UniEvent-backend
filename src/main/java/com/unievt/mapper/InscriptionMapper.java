package com.unievt.mapper;

import com.unievt.dto.inscription.InscriptionCreateDTO;
import com.unievt.dto.inscription.InscriptionResponseDTO;
import com.unievt.dto.inscription.InscriptionUpdateDTO;
import com.unievt.entity.Inscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InscriptionMapper {

    @Mapping(target = "etudiant.id", source = "etudiantId")
    @Mapping(target = "evenement.id", source = "evenementId")
    Inscription toEntity(InscriptionCreateDTO dto);

    @Mapping(target = "etudiantId", expression = "java(inscription.getEtudiant()!=null? inscription.getEtudiant().getId() : null)")
    @Mapping(target = "etudiantName", expression = "java(inscription.getEtudiant()!=null? inscription.getEtudiant().toString() : null)")
    @Mapping(target = "evenementId", expression = "java(inscription.getEvenement()!=null? inscription.getEvenement().getId() : null)")
    @Mapping(target = "evenementTitle", expression = "java(inscription.getEvenement()!=null? inscription.getEvenement().toString() : null)")
    InscriptionResponseDTO toResponseDTO(Inscription inscription);

    void updateEntityFromDTO(InscriptionUpdateDTO dto, @MappingTarget Inscription entity);
}
