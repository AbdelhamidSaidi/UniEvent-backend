package com.unievt.evaluation.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResponseDTO {

    private Long id;
    private Integer note;
    private String commentaire;
    private LocalDateTime dateEvaluation;
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
}
