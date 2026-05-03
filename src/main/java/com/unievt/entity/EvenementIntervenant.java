package com.unievt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evenement_intervenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvenementIntervenant {

    @EmbeddedId
    private EvenementIntervenantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("evenementId")
    @JoinColumn(name = "id_evenement")
    private Evenement evenement;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("intervenantId")
    @JoinColumn(name = "id_intervenant")
    private Intervenant intervenant;

}
