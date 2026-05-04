package com.unievt.reservation.dto;

import com.unievt.enums.StatutReservationEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {

    private Long id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private StatutReservationEnum statut;
    private String commentaire;
    private LocalDateTime dateCreation;
    private Long salleId;
    private String salleNom;
    private Long demandeurId;
    private String demandeurNom;
    private String demandeurPrenom;
    private Long approbateurId;
    private String approbateurNom;
}
