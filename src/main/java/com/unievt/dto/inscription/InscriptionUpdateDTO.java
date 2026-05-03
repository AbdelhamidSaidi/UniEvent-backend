package com.unievt.dto.inscription;

import com.unievt.enums.StatutInscriptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionUpdateDTO {
    private StatutInscriptionEnum statut;
}
