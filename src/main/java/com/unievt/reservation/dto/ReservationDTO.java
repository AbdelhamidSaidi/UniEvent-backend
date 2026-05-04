package com.unievt.reservation.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String commentaire;
    private Long salleId;
    private Long demandeurId;
}
