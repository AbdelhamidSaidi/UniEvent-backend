package com.unievt.dto.inscription;

import com.unievt.enums.StatutInscriptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionCreateDTO {
    private LocalDateTime dateInscription;
    private StatutInscriptionEnum statut;
    private String qrCode;
    private Boolean present;
    private Long etudiantId;
    private Long evenementId;
}
