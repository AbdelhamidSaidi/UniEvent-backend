package com.unievt.salle.entity;

import com.unievt.enums.StatutSalleEnum;
import com.unievt.enums.TypeSalleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "salle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_salle")
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String batiment;

    private Integer etage;

    private Integer capacite;

    @Enumerated(EnumType.STRING)
    private TypeSalleEnum type;

    @ElementCollection
    @CollectionTable(name = "salle_equipements", joinColumns = @JoinColumn(name = "id_salle"))
    @Column(name = "equipement")
    private List<String> equipements;

    @Column(name = "accessible_pmr")
    private Boolean accessiblePMR = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSalleEnum statut = StatutSalleEnum.DISPONIBLE;

    @ElementCollection
    @CollectionTable(name = "salle_photos", joinColumns = @JoinColumn(name = "id_salle"))
    @Column(name = "photo")
    private List<String> photos;
}
