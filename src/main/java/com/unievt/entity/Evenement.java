package com.unievt.entity;

import com.unievt.enums.CategorieEnum;
import com.unievt.enums.StatutEvenementEnum;
import com.unievt.enums.TypeEvenementEnum;
import com.unievt.enums.VisibiliteEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evenement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titre")
    private String titre;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "categorie")
    @Enumerated(EnumType.STRING)
    private CategorieEnum categorie;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "capacite")
    private Integer capacite;

    @Column(name = "affiche")
    private String affiche;

    @Column(name = "statut")
    @Enumerated(EnumType.STRING)
    private StatutEvenementEnum statut;

    @Column(name = "visibilite")
    @Enumerated(EnumType.STRING)
    private VisibiliteEnum visibilite;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TypeEvenementEnum type;

    @Column(name = "lien_visio")
    private String lienVisio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_club")
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organisateur")
    private Utilisateur organisateur;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscription> inscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvenementIntervenant> intervenants = new ArrayList<>();

}
