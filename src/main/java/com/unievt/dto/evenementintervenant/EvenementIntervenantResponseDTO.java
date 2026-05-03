package com.unievt.dto.evenementintervenant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvenementIntervenantResponseDTO {
    private Long evenementId;
    private Long intervenantId;
    private String intervenantName;
    private String evenementTitle;
}
