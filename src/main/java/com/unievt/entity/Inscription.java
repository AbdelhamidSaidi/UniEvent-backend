package com.unievt.entity;

import com.unievt.enums.StatutInscriptionEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_inscription")
    private LocalDateTime dateInscription;

    @Column(name = "statut")
    @Enumerated(EnumType.STRING)
    private StatutInscriptionEnum statut;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "present")
    private Boolean present;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etudiant")
    private Utilisateur etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evenement")
    private Evenement evenement;

}
