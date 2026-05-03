package com.unievt.dto.evenementintervenant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvenementIntervenantCreateDTO {
    private Long evenementId;
    private Long intervenantId;
}
