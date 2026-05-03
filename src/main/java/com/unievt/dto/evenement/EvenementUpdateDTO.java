package com.unievt.dto.evenement;

import com.unievt.enums.StatutEvenementEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvenementUpdateDTO {
    private StatutEvenementEnum statut;
}
