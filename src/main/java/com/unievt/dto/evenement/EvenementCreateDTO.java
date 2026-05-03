package com.unievt.dto.evenement;

import com.unievt.enums.CategorieEnum;
import com.unievt.enums.StatutEvenementEnum;
import com.unievt.enums.TypeEvenementEnum;
import com.unievt.enums.VisibiliteEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvenementCreateDTO {
    private String titre;
    private String description;
    private CategorieEnum categorie;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Integer capacite;
    private String affiche;
    private StatutEvenementEnum statut;
    private VisibiliteEnum visibilite;
    private TypeEvenementEnum type;
    private String lienVisio;
    private Long clubId;
    private Long organisateurId;
}
