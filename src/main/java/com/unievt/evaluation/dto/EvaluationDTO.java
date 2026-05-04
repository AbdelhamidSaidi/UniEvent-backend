package com.unievt.evaluation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationDTO {

    private Integer note; // 1 à 5
    private String commentaire;
    private Long etudiantId;
}
