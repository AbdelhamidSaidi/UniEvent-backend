package com.unievt.evaluation.mapper;

import com.unievt.evaluation.dto.EvaluationDTO;
import com.unievt.evaluation.dto.EvaluationResponseDTO;
import com.unievt.evaluation.entity.Evaluation;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EvaluationMapper {

    @Mapping(target = "etudiant", ignore = true)
    Evaluation toEntity(EvaluationDTO dto);

    @Mapping(source = "etudiant.id", target = "etudiantId")
    @Mapping(source = "etudiant.nom", target = "etudiantNom")
    @Mapping(source = "etudiant.prenom", target = "etudiantPrenom")
    EvaluationResponseDTO toResponseDTO(Evaluation entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "etudiant", ignore = true)
    void updateEntityFromDTO(EvaluationDTO dto, @MappingTarget Evaluation entity);
}
