package com.unievt;

import com.unievt.enums.RoleEnum;
import com.unievt.enums.CategorieEnum;
import com.unievt.enums.StatutReservationEnum;
import com.unievt.enums.StatutSalleEnum;
import com.unievt.enums.StatutEvenementEnum;
import com.unievt.enums.TypeNotifEnum;
import com.unievt.enums.TypeSalleEnum;
import com.unievt.enums.TypeEvenementEnum;
import com.unievt.enums.VisibiliteEnum;
import com.unievt.dto.EvaluationDTO;
import com.unievt.dto.EvaluationResponseDTO;
import com.unievt.entity.Evaluation;
import com.unievt.mapper.EvaluationMapper;
import com.unievt.repository.EvaluationRepository;
import com.unievt.entity.Evenement;
import com.unievt.repository.EvenementRepository;
import com.unievt.dto.NotificationDTO;
import com.unievt.dto.NotificationResponseDTO;
import com.unievt.entity.Notification;
import com.unievt.mapper.NotificationMapper;
import com.unievt.repository.NotificationRepository;
import com.unievt.dto.ReservationDTO;
import com.unievt.dto.ReservationResponseDTO;
import com.unievt.entity.Reservation;
import com.unievt.mapper.ReservationMapper;
import com.unievt.repository.ReservationRepository;
import com.unievt.dto.SalleDTO;
import com.unievt.entity.Salle;
import com.unievt.mapper.SalleMapper;
import com.unievt.repository.SalleRepository;
import com.unievt.entity.Utilisateur;
import com.unievt.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SafaaModulesIntegrationTests {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private SalleRepository salleRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private SalleMapper salleMapper;
    @Autowired
    private ReservationMapper reservationMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private EvaluationMapper evaluationMapper;

    private Utilisateur etudiant;
    private Utilisateur demandeur;
    private Utilisateur approbateur;
    private Salle salle;
    private Evenement evenement;

    @BeforeEach
    void setUp() {
        // Note (Strike, refactor): RoleEnum.ETUDIANT and RoleEnum.PRESIDENT_CLUB were
        // removed because they were identity facts, not permission roles. "Étudiant"
        // is now derived from the etudiant table; "club president" from Club.president
        // FK. The role field is nullable; only ADMIN/DOYEN/RESPONSABLE_EVENEMENTS remain.
        etudiant = utilisateurRepository.save(Utilisateur.builder()
                .nom("Etudiant")
                .prenom("One")
                .email("safaa.etudiant@unievt.ma")
                .motDePasse("pass")
                // role left null — étudiants don't carry a permission role by default
                .actif(true)
                .build());

        demandeur = utilisateurRepository.save(Utilisateur.builder()
                .nom("Demandeur")
                .prenom("One")
                .email("safaa.demandeur@unievt.ma")
                .motDePasse("pass")
                .role(RoleEnum.RESPONSABLE_EVENEMENTS) // closest surviving role for an event organiser
                .actif(true)
                .build());

        approbateur = utilisateurRepository.save(Utilisateur.builder()
                .nom("Approbateur")
                .prenom("One")
                .email("safaa.approbateur@unievt.ma")
                .motDePasse("pass")
                .role(RoleEnum.DOYEN)
                .actif(true)
                .build());

        salle = salleRepository.save(Salle.builder()
                .nom("Amphi A")
                .batiment("B1")
                .etage(1)
                .capacite(120)
                .type(TypeSalleEnum.AMPHI)
                .accessiblePMR(true)
                .statut(StatutSalleEnum.DISPONIBLE)
                .equipements(List.of("Projecteur", "Sono"))
                .build());

        evenement = evenementRepository.save(Evenement.builder()
                .titre("Evenement Safaa")
                .description("Evenement de test")
                .categorie(CategorieEnum.CONFERENCE)
                .dateDebut(LocalDateTime.now().plusDays(7))
                .dateFin(LocalDateTime.now().plusDays(7).plusHours(2))
                .capacite(100)
                .statut(StatutEvenementEnum.APPROUVE)
                .visibilite(VisibiliteEnum.UNIVERSITE)
                .type(TypeEvenementEnum.CLUB)
                .organisateur(demandeur)
                .build());
    }

    @Test
    void salleRepositoryQueriesWork() {
        salleRepository.save(Salle.builder()
                .nom("Lab Info")
                .batiment("B2")
                .capacite(30)
                .type(TypeSalleEnum.SALLE_INFORMATIQUE)
                .statut(StatutSalleEnum.MAINTENANCE)
                .accessiblePMR(false)
                .build());

        assertEquals(1, salleRepository.findByType(TypeSalleEnum.AMPHI).size());
        assertEquals(1, salleRepository.findByStatut(StatutSalleEnum.MAINTENANCE).size());
        assertEquals(1, salleRepository.findByCapaciteGreaterThanEqual(100).size());
    }

    @Test
    void reservationRepositoryQueriesWork() {
        Reservation reservation = reservationRepository.save(Reservation.builder()
                .dateDebut(LocalDateTime.now().plusDays(1))
                .dateFin(LocalDateTime.now().plusDays(1).plusHours(2))
                .commentaire("Premiere demande")
                .statut(StatutReservationEnum.EN_ATTENTE)
                .salle(salle)
                .evenement(evenement)
                .demandeur(demandeur)
                .approbateur(approbateur)
                .build());

        assertEquals(StatutReservationEnum.EN_ATTENTE, reservation.getStatut());
        assertEquals(1, reservationRepository.findByStatut(StatutReservationEnum.EN_ATTENTE).size());
        assertEquals(1, reservationRepository.findByEvenementId(evenement.getId()).size());
        assertEquals(1, reservationRepository.findBySalleId(salle.getId()).size());
        assertEquals(1, reservationRepository.findByDemandeurId(demandeur.getId()).size());
    }

    @Test
    void notificationRepositoryQueriesWork() {
        notificationRepository.save(Notification.builder()
                .titre("Validation")
                .message("Votre reservation est validee")
                .type(TypeNotifEnum.EMAIL)
                .destinataire(demandeur)
                .evenement(evenement)
                .lu(false)
                .build());

        notificationRepository.save(Notification.builder()
                .titre("Rappel")
                .message("Debut de l'evenement dans 24h")
                .type(TypeNotifEnum.PUSH)
                .destinataire(demandeur)
                .evenement(evenement)
                .lu(true)
                .build());

        assertEquals(2, notificationRepository.findByDestinataireId(demandeur.getId()).size());
        assertEquals(1, notificationRepository.findByDestinataireIdAndLuFalse(demandeur.getId()).size());
        assertEquals(2, notificationRepository.findByEvenementId(evenement.getId()).size());
    }

    @Test
    void evaluationRepositoryQueryWork() {
        Reservation reservation = reservationRepository.save(Reservation.builder()
                .dateDebut(LocalDateTime.now().plusDays(2))
                .dateFin(LocalDateTime.now().plusDays(2).plusHours(2))
                .statut(StatutReservationEnum.APPROUVEE)
                .commentaire("Reservation pour evaluation")
                .salle(salle)
                .evenement(evenement)
                .demandeur(demandeur)
                .approbateur(approbateur)
                .build());

        evaluationRepository.save(Evaluation.builder()
                .note(5)
                .commentaire("Excellent")
                .etudiant(etudiant)
                .reservation(reservation)
                .build());

        evaluationRepository.save(Evaluation.builder()
                .note(4)
                .commentaire("Tres bien")
                .etudiant(etudiant)
                .reservation(reservation)
                .build());

        assertEquals(2, evaluationRepository.findByEtudiantId(etudiant.getId()).size());
        assertEquals(2, evaluationRepository.findByReservationId(reservation.getId()).size());
        assertEquals(2, evaluationRepository.findByReservationEvenementId(evenement.getId()).size());
    }

    @Test
    void salleMapperUpdateIgnoresNullValues() {
        Salle entity = Salle.builder()
                .nom("Salle initiale")
                .batiment("B3")
                .statut(StatutSalleEnum.DISPONIBLE)
                .accessiblePMR(false)
                .build();

        SalleDTO update = SalleDTO.builder()
                .nom("Salle renommee")
                .batiment(null)
                .build();

        salleMapper.updateEntityFromDTO(update, entity);

        assertEquals("Salle renommee", entity.getNom());
        assertEquals("B3", entity.getBatiment());
        assertEquals(StatutSalleEnum.DISPONIBLE, entity.getStatut());
    }

    @Test
    void reservationMapperHandlesRelationsAndUpdate() {
        ReservationDTO dto = ReservationDTO.builder()
                .dateDebut(LocalDateTime.now().plusDays(3))
                .dateFin(LocalDateTime.now().plusDays(3).plusHours(1))
                .commentaire("Test mapper")
                .evenementId(evenement.getId())
                .salleId(salle.getId())
                .demandeurId(demandeur.getId())
                .build();

        Reservation entity = reservationMapper.toEntity(dto);
        assertNull(entity.getSalle());
        assertNull(entity.getEvenement());
        assertNull(entity.getDemandeur());
        assertNull(entity.getApprobateur());

        entity.setEvenement(evenement);
        entity.setSalle(salle);
        entity.setDemandeur(demandeur);
        entity.setApprobateur(approbateur);
        ReservationResponseDTO response = reservationMapper.toResponseDTO(entity);

        assertEquals(evenement.getId(), response.getEvenementId());
        assertEquals("Evenement Safaa", response.getEvenementTitre());
        assertEquals(salle.getId(), response.getSalleId());
        assertEquals(demandeur.getId(), response.getDemandeurId());
        assertEquals(approbateur.getId(), response.getApprobateurId());
        assertEquals("Approbateur", response.getApprobateurNom());

        ReservationDTO partialUpdate = ReservationDTO.builder()
                .commentaire("Commentaire maj")
                .dateDebut(null)
                .build();
        reservationMapper.updateEntityFromDTO(partialUpdate, entity);
        assertEquals("Commentaire maj", entity.getCommentaire());
        assertNotNull(entity.getDateDebut());
        assertEquals(evenement.getId(), entity.getEvenement().getId());
        assertEquals(salle.getId(), entity.getSalle().getId());
    }

    @Test
    void notificationAndEvaluationMappersMapIdentityFields() {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .titre("Titre")
                .message("Message")
                .type(TypeNotifEnum.SMS)
                .destinataireId(etudiant.getId())
                .evenementId(evenement.getId())
                .build();

        Notification notification = notificationMapper.toEntity(notificationDTO);
        assertNull(notification.getDestinataire());
        assertNull(notification.getEvenement());
        notification.setDestinataire(etudiant);
        notification.setEvenement(evenement);
        NotificationResponseDTO notificationResponse = notificationMapper.toResponseDTO(notification);
        assertEquals(etudiant.getId(), notificationResponse.getDestinataireId());
        assertEquals("Etudiant", notificationResponse.getDestinataireNom());
        assertEquals(evenement.getId(), notificationResponse.getEvenementId());

        EvaluationDTO evaluationDTO = EvaluationDTO.builder()
                .note(3)
                .commentaire("Correct")
                .etudiantId(etudiant.getId())
                .reservationId(1L)
                .build();
        Evaluation evaluation = evaluationMapper.toEntity(evaluationDTO);
        assertNull(evaluation.getEtudiant());
        assertNull(evaluation.getReservation());

        Reservation reservation = Reservation.builder()
                .id(1L)
                .evenement(evenement)
                .build();
        evaluation.setEtudiant(etudiant);
        evaluation.setReservation(reservation);
        EvaluationResponseDTO evaluationResponse = evaluationMapper.toResponseDTO(evaluation);
        assertEquals(etudiant.getId(), evaluationResponse.getEtudiantId());
        assertEquals("One", evaluationResponse.getEtudiantPrenom());
        assertEquals(1L, evaluationResponse.getReservationId());
        assertEquals(evenement.getId(), evaluationResponse.getEvenementId());
    }
}
