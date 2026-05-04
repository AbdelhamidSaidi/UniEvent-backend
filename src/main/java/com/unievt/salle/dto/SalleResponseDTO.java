package com.unievt.salle.dto;

import com.unievt.enums.StatutSalleEnum;
import com.unievt.enums.TypeSalleEnum;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalleResponseDTO {

    private Long id;
    private String nom;
    private String batiment;
    private Integer etage;
    private Integer capacite;
    private TypeSalleEnum type;
    private List<String> equipements;
    private Boolean accessiblePMR;
    private StatutSalleEnum statut;
    private List<String> photos;
}
